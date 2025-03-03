#include <SoftwareSerial.h>
#include <TaskScheduler.h>
#include <DFRobotDFPlayerMini.h>
#include <ArduinoJson.h>

#define MIC A0   // 마이크
#define MLED 10  // 조명 LED(릴레이 모듈)
#define ACT 8    // 모션인식
#define SLED 9   // 동작 LED

#define SOUND_ON 0       // 켜졌습니다.
#define SOUND_OFF 1      // 꺼졌습니다.
#define SOUND_SAVE 2     // 적용되었다
#define SOUND_REQSAFE 3  // 안전한가요
#define SOUND_ALERT 4    // 너 그러면 안되
#define SOUND_AUTOON 5   // 스마트감지 동작중입니다
#define SOUND_AUTOOFF 6  // 스마트감지 해제되었습니다
#define SOUND_MAX 7
#define RECVDATA_MAX 1024

#define PLACE_RESTROOM 'R'  // 화장실
#define PLACE_FLOOR 'F'     // 거실
#define PLACE_BEDROOM 'B'   // 침실

DFRobotDFPlayerMini DFPlayer;  // 객체 생성, dfplayer 활용

// typedef struct {
//   char HEADER[10];  // key
//   char DATA[1000];   // data
//   //char SCMD[1];
// } BT_HEADER;

///////////////////////////////////////
long g_shortDelay = 10000;  // 10초 -> 3분
long g_longDelay = 30000;   // 30초 -> 30분
//int g_midDelay = 60000;	  //

//String g_soundFileList[SOUND_MAX];  // 음향 파일리스트
char g_recvData[RECVDATA_MAX];  // 블루투스로 받는 데이터
int g_recvDataIndex = 0;
char g_sendData[RECVDATA_MAX];  // 블루투스로 보내는 데이터
int g_sendDataIndex = 0;
char g_placeType = PLACE_RESTROOM;  // 장소 - 화장실"R", 거실"F", 침실:"B"
bool g_autoDetectYn = true;         // 스마트 감지 사용 여부
long g_actStartTime;                // 감지 시작 시간 체크
char g_sLedStatus = '0';            // 상태값 : 0:off, 1:on
char g_mLedStatus = '0';            // 상태값 : 0:off, 1:on
char g_premLedStatus;               // 이전 상태값 저장

//bool g_btSendDataYn;		 	// 보낼 정보 여부
//bool g_actYn=false;
bool g_sLedBlinkYn = true;       // 깜박임 여부
int g_sLedBlinkInterval = 1000;  // 깜박임 주기
int g_alretCount = 0;            // 큰 소리 감지 횟수

int g_prevClap;             // 이전에 박수를 쳤는지를 표현하는 변수
int g_currClap;             // 현재 박수를 쳤는지를 표현하는 변수
long g_prevClapTime = 0;    // 이전에 박수를 친 시간을 기록하는 변수
long g_currClapTime = 0;    // 현재 박수를 친 시간을 기록하는 변수
long g_mLedChangeTime = 0;  // 조명의 On/Off가 바뀐 시간을 기록하는 변수

Scheduler runner;
///////////////////////////////////////
void devBtConnect();  // 함수 선언
void devBtDisconnect();
char* devBtSend();
char* devBtRecv();
void devSpPlay(int soundTrackindex);
int devMicRecv();
bool devActRecv();
void devSLedOn();
void devSLedOff();
void devSLedBlink();
void devMLedOn();
void devMLedOff();

Task t_devBtRecv(2000, TASK_FOREVER, []() {  // 블루투스 쓰레드
  devBtRecv();

  if (g_recvDataIndex > 0) {
    //BT_HEADER* pvBtHeader = (BT_HEADER*)g_recvData;

    StaticJsonDocument<1000> doc;  // 데이터 파싱
    char* json = (char*)g_recvData;
    auto error = deserializeJson(doc, json);
    if (error) {
      Serial.print("deserializeJson() failed with code ");
      Serial.println(error.c_str());
    }

    String header = doc["header"];
    String data = doc["data"];

    if (header == "light") {
      if (data == "On") {
        g_autoDetectYn = true;
        devMLedOn();
        devSpPlay(SOUND_AUTOON);
      }
      if (data == "Off") {
        g_autoDetectYn = false;
        devMLedOff();
        devSpPlay(SOUND_AUTOOFF);
      }
    }
    if (header == "setinfo") {
    }

    g_recvDataIndex = 0;
  }
});

Task t_devBtSend(3000, TASK_FOREVER, []() {  // 블루투스 전송 쓰레드
  Serial1.print(devBtSend());
});

Task t_devMicRecv(0, TASK_FOREVER, []() {  // 음성인식 쓰레드
  if (devMicRecv() > 540) { // 감도
    g_currClap = 1;
  } else {
    g_currClap = 0;
  }
  g_currClapTime = millis();

  if (g_currClap == 1) {
    if (
      (g_currClapTime > g_prevClapTime + 200) &&  // 이전 박수 0.2초 후에 박수소리가 들린거면
      (g_prevClap == 0) &&                        // 이전에 박수소리가 없었으면(큰소리가 계속 들리는경우 불이 켜지지 않음)
      (g_currClapTime < g_prevClapTime + 800) &&  // 이전 박수 0.8초 이전에 박수소리가 들린거면
      (g_currClapTime > g_mLedChangeTime + 1000)  // 전등 상태가 달라진지 1초 이후이면(박수를 세번쳤을때 반응이 없도록)
    ) {
      // 위 조건을 모두 만족한 경우
      g_autoDetectYn = !g_autoDetectYn;  // 스마트감지 상태를 토글, 꺼져있었으면 켜주고, 켜져있었으면 꺼주는 상태
      if (g_autoDetectYn == true) {
        devMLedOn();
        devSpPlay(SOUND_AUTOON);
        Serial.println("박수짝");
      } else if (g_autoDetectYn == false) {
        devMLedOff();
        devSpPlay(SOUND_AUTOOFF);
        Serial.println("박수짝");
      }
      g_mLedChangeTime = g_currClapTime;  // 현재의 시간을 changeTime으로 함
    }

    g_prevClapTime = g_currClapTime;  // 현재 박수시간을 이전 박수시간으로 해줌
  }
  g_prevClap = g_currClap;  // 현재 박수소리 상태를 이전 박수소리 상태로 해줌
});

Task t_devActRecv(500, TASK_FOREVER, []() {  //  모션인식 쓰레드
  if (devActRecv() == false) {
    return;
  }
  g_actStartTime = millis();

  if (g_autoDetectYn == true) {
    devMLedOn();
    if (g_premLedStatus != g_mLedStatus) {
      devSpPlay(SOUND_ON);
      g_premLedStatus = g_mLedStatus;
    }
  }
});

Task t_timer(1000, TASK_FOREVER, []() {  // 타이머 쓰레드
  // 자동감지 상태면 idle시간후 자동꺼짐
  if (g_autoDetectYn == true) {
    long currTime = millis();
    long delayTime = g_longDelay;  // default : long

    if (g_placeType == PLACE_RESTROOM)  // short
      delayTime = g_shortDelay;

    if (currTime - g_actStartTime >= delayTime) {
      devMLedOff();
      if (g_premLedStatus != g_mLedStatus) {
        devSpPlay(SOUND_OFF);
        g_premLedStatus = g_mLedStatus;
      }
    }

    //Serial.println(currTime - g_actStartTime);
    //Serial.println(delayTime);
  }
});

Task t_sLedBlink(g_sLedBlinkInterval, TASK_FOREVER, []() {  // 경고 LED 쓰레드
  // 특정 샘플이 실행될 때 g_sLedBlinkYn를 true로 만드는 코드가 필요 예) 엄청나게 큰소리가 지속 또는 그 후 응답이 없을 때 g_sLedBlinkYn를 true로
  if (g_sLedBlinkYn == false) {
    return;
  } else if (g_sLedBlinkYn == true) {
    devSLedBlink();
  }
});

////////////////////////////////////////////////////////////////////
void setup() {
  // g_soundFileList[SOUND_ON] = "";
  // g_soundFileList[SOUND_OFF] = "";
  // g_soundFileList[SOUND_SAVE] = "";
  // g_soundFileList[SOUND_REQSAFE] = "";
  // g_soundFileList[SOUND_ALERT] = "";
  // g_soundFileList[SOUND_AUTOON] = "";
  // g_soundFileList[SOUND_AUTOOFF] = "";

  Serial.begin(9600);
  Serial1.begin(9600);  // 블루투스 연결
  Serial2.begin(9600);  // 스피커 연결

  pinMode(MLED, OUTPUT);
  pinMode(MIC, INPUT);
  pinMode(ACT, INPUT);
  pinMode(SLED, OUTPUT);

  if (!DFPlayer.begin(Serial2), false) {  //객체 초기화
    Serial.println(F("Unable to begin:"));
    Serial.println(F("1.Please recheck the connection!"));
    Serial.println(F("2.Please insert the SD card!"));
    while (true)
      ;
  }
  delay(1);
  DFPlayer.volume(17);  // 볼륨을 조절합니다. 0~30까지 설정이 가능합니다.

  runner.init();
  runner.addTask(t_devBtRecv);
  runner.addTask(t_devBtSend);
  runner.addTask(t_devMicRecv);
  runner.addTask(t_devActRecv);
  runner.addTask(t_timer);
  runner.addTask(t_sLedBlink);

  t_devBtRecv.enable();
  t_devBtSend.enable();
  t_devMicRecv.enable();
  t_devActRecv.enable();
  t_timer.enable();
  t_sLedBlink.enable();
}

void loop() {
  runner.execute();
}

void devBtConnect() {  // 블루투스 연결
  if (Serial1.available() == false) {
    Serial1.begin(9600);
  }
}

void devBtDisconnect() {  // 블루투스 연결 해제
  if (Serial1.available() == true) {
    Serial1.print("AT+DISC\r\n");
  }
}

char* devBtSend() {  // 블루투스에 값을 보냄 , 보낼 값 작성해야함
  if (Serial1.available() == false) {
    devBtConnect();
  }

  // String payload = (String)g_placeType;     // 장소 - 화장실"R", 거실"F", 침실:"B"
  // payload += "," + (String)g_autoDetectYn;  // 스마트 감지 사용 여부
  // payload += "," + (String)g_mLedStatus;    // 상태값 : 0:off, 1:on
  // payload += "/";

  //json 만들기
  String json_tmp = "{";

  json_tmp += "\"place\":\"";
  json_tmp += (String)g_placeType;
  json_tmp += "\",";

  json_tmp += "\"smartdetect\":\"";
  json_tmp += (String)g_autoDetectYn;
  json_tmp += "\",";

  json_tmp += "\"light\":\"";
  json_tmp += (String)g_mLedStatus;
  json_tmp += "\"}";

  Serial.println(json_tmp);
  g_sendDataIndex = json_tmp.length();
  json_tmp.toCharArray(g_sendData, g_sendDataIndex + 1);
  g_sendData[g_sendDataIndex] = 0x00;

  return g_sendData;
}

char* devBtRecv() {  // 블루투스가 값을 받음
  if (Serial1.available() == false) {
    devBtConnect();
  }

  String inString = Serial1.readStringUntil('\n');
  if (inString.length() == 0)
    return "";

  Serial.println(inString);
  g_recvDataIndex = inString.length();
  inString.toCharArray(g_recvData, g_recvDataIndex + 1);
  g_recvData[g_recvDataIndex] = 0x00;

  return g_recvData;
}

// char* devBtRecv() {  // 블루투스가 값을 받음
//   if (Serial1.available() == false) {
//     devBtConnect();
//   }
//   if (Serial1.available() == true) {
//     int tempRecvDataIndex = g_recvDataIndex;
//     g_recvDataIndex = 0;
//     while (Serial1.available()) {
//       g_recvData[tempRecvDataIndex++] = Serial1.read();
//       if (tempRecvDataIndex + 1 > RECVDATA_MAX)
//         break;
//     }
//     Serial.println(g_recvData);
//     g_recvData[tempRecvDataIndex] = 0x00;
//     g_recvDataIndex = tempRecvDataIndex;
//     return g_recvData;
//   }
//   return "";
// }

void devSpPlay(int soundTrackindex) {  // 스피커 출력
  if (soundTrackindex < 0)
    return;

  if (DFPlayer.readState() == -1) {  // 스피커 소리가 이미 나는 중인지 체크
    DFPlayer.playMp3Folder(soundTrackindex + 1);
  }
}

int devMicRecv() {  // 마이크 수신만, 일단 임시로 사용
  int micValue = analogRead(MIC);

  return micValue;
}

bool devActRecv() {                 // 모션센서 수신, 임시로 사용
  int actState = digitalRead(ACT);  //
  if (actState == 0) {
    return false;
  }
  if (actState == 1) {
    return true;
  }
}

void devSLedOn() {  // 상태 LED 켜기
  digitalWrite(SLED, HIGH);
  g_sLedStatus = '1';
}

void devSLedOff() {  //	상태LED 끄기
  digitalWrite(SLED, LOW);
  g_sLedStatus = '0';
}

void devSLedBlink() {  //상태LED 깜박이기
  if (g_sLedStatus == '0') {
    devSLedOn();
  } else if (g_sLedStatus == '1') {
    devSLedOff();
  }
}

void devMLedOn() {  //조명LED 켜기
  digitalWrite(MLED, HIGH);
  g_mLedStatus = '1';
}

void devMLedOff() {  //조명LED 끄기
  digitalWrite(MLED, LOW);
  g_mLedStatus = '0';
}