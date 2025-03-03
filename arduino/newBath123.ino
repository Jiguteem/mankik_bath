#include <OneWire.h>  //온도센서 라이브러리
#include <Wire.h>
#include <SoftwareSerial.h>  // 블루투스 라이브러리
#include <ArduinoJson.h>     // JSON 파싱 라이브러리
#include <string.h>
#include <TaskScheduler.h>  // 스케줄러 라이브러리

// 핀 번호 정의
#define TEMPSENCOR 2  //방수 온도센서
#define TRIG 3        //초음파 센서 보내는 핀
#define ECHO 4        //초음파 센서 받는 핀
#define HEATRAYS 8    //high시 on
#define CVALVE 9      //high시 on
#define HVALVE 10     //high시 on
#define SVALVE 11     //high시 on

OneWire ds(TEMPSENCOR);  //온도센서 변수

int debug = 1;  // 1은 로그 나옴

typedef struct {
  // 서버로 부터 받은 데이터
  float degree;
  int waterlevel;
  int cap;
  int hvalve;
  int cvalve;
  int cleantime;
  int heatrays;
  // 서버로 부터 받은 시간
  int year;
  int month;
  int day;
  int hour;
  int minute;
  int sec;
} CONTROL_DATA;

typedef struct {
  // 서버로 부터 보낼 데이터
  int bathstate = 0;   // 현재 동작 (0: 없음 1: 청소 2:물받기 3:물유지)
  int degree = 0;      // 현재 온도
  int waterlevel = 0;  // 현재 수위(퍼센트)
  int cap = 0;         // 마개 상태
  int hvalve = 0;      // 온수밸브 상태(닫힘 0, 열림 1)
  int cvalve = 0;      // 냉수밸브 상태
  int svalve = 0;      // 스프링클러밸브 상태
  int heatrays = 0;    // 열선 상태
  int record = 0;      // 이력 상태
} INFO_DATA;

typedef struct {
  // 서버로 부터 보낼 데이터
  int useTime;        // 목욕 진행 시간
  char endTime[100];  // 목욕 끝난 시간
  // 현재 시간
  int year;
  int month;
  int day;
  int hour;
  int minute;
  int sec;
  char startTime[100];
} HISTORY_DATA;

int g_bathLevel;              // 욕조 깊이 테스트를 위해 설정해줌
unsigned long g_startTime;    //목욕 시작 시간 체크
unsigned long g_timeVal = 0;  // 목욕 시간 저장
int g_preCleantime;           // 청소 진행 체크 변수
int g_bathID;                 // 바스 id
int g_userID;                 // 유저 id

// 이전 상태값 저장 함수
int g_preCap;
int g_preHvalve;
int g_preCvalve;
int g_preSvalve;
int g_preDegree;
unsigned int g_uid;   // 명령 번호 uid
int g_sendCap = -1;   // 블루투스 요청 보내기 변수, 0 : 꺼짐, 1: 켜짐
int g_stopState = 0;  // 정지 여부 체크, 0 : 유지, 1 : 정지

// 함수 선언만
Scheduler runner;
CONTROL_DATA controlData;
INFO_DATA infoData;
HISTORY_DATA historyData;

/////////////////////////////////////////
void devClear();
float degreeCal();
float depthCal();
void devOnHotvalve();
void devOffHotvalve();
void devOnColdvalve();
void devOffColdvalve();
void devOnSpkler();
void devOffSpkler();
void devOnHeatrays();
void devOffHeatrays();

Task t_devCapSend(2000, TASK_FOREVER, []() {  // 마개 요청 보냄 offcap, oncap 대신 사용
  if (g_sendCap == 1) {
    Serial2.print("on");
    g_sendCap == -1;
  }
  if (g_sendCap == 0) {
    Serial2.print("off");
    g_sendCap == -1;
  }
});

Task t_devCapRecv(2000, TASK_FOREVER, []() {  // 마개 요청 받음
  String payload = "";
  Serial2.print("cap");

  while (Serial2.available()) {
    char btread;
    btread = Serial2.read();
    payload += btread;  //입력 받은 데이터를 1단어씩 저장
    //delay(15);          //수신 대기
  }

  Serial.print("현재 마개 상태 : ");
  if (debug) {
    Serial.println(payload);
  }

  if (payload.equals("on")) {
    infoData.cap = 1;
  }
  if (payload.equals("off")) {
    infoData.cap = 0;
  }
});

Task t_devBtControl(2000, TASK_FOREVER, []() {  // 블루투스 컨트롤 받기
  int parsingBathid;
  int parsingUserid;
  float parsingDegree;
  int parsingWaterlevel;
  int parsingCap;
  int parsingHvalve;
  int parsingCvalve;
  int parsingCleantime;
  String payload = "";

  Serial1.print("control");
  //delay(1000);

  while (Serial1.available()) {
    char btread;
    btread = Serial1.read();
    payload += btread;  //입력 받은 데이터를 1단어씩 저장
    //delay(15);          //수신 대기
  }
  if (debug) {
    Serial.println(payload);
  }

  // 파싱 후 리턴
  char* tmp = (char*)payload.c_str();

  parsingBathid = atof(strtok(tmp, "/"));
  parsingUserid = atoi(strtok(NULL, "/"));
  parsingDegree = atoi(strtok(NULL, "/"));
  parsingWaterlevel = atoi(strtok(NULL, "/"));
  parsingCap = atoi(strtok(NULL, "/"));
  parsingHvalve = atoi(strtok(NULL, "/"));
  parsingCvalve = atoi(strtok(NULL, "/"));
  parsingCleantime = atoi(strtok(NULL, "/"));

  const char* date = strtok(NULL, "/");

  if (date != 0) {  // 잘못된 데이터가 안온다면
    g_bathID = parsingBathid;
    g_userID = parsingUserid;
    controlData.degree = parsingDegree;
    controlData.waterlevel = parsingWaterlevel;
    controlData.cap = parsingCap;
    controlData.hvalve = parsingHvalve;
    controlData.cvalve = parsingCvalve;
    controlData.cleantime = parsingCleantime;

    String a = "";

    for (int i = 0; i < 4; i++) {
      a += date[i];
    }
    controlData.year = a.toInt();
    a = "";

    for (int i = 5; i < 7; i++) {
      a += date[i];
    }
    controlData.month = a.toInt();
    a = "";

    for (int i = 8; i < 10; i++) {
      a += date[i];
    }
    controlData.day = a.toInt();
    a = "";

    for (int i = 11; i < 13; i++) {
      a += date[i];
    }
    controlData.hour = a.toInt();
    a = "";

    for (int i = 14; i < 16; i++) {
      a += date[i];
    }
    controlData.minute = a.toInt();
    a = "";

    for (int i = 17; i < 19; i++) {
      a += date[i];
    }
    controlData.sec = a.toInt();
  }
});

Task t_devBtInfo(2000, TASK_FOREVER, []() {            // 블루투스 인포 주기, 스탑도 동시 제어
  if (controlData.degree == 99 && g_stopState == 0) {  // 정지 상태일 때 한번 history 보냄
    g_stopState = 1;
    infoData.bathstate = 0;
    infoData.record = 14;

    // 목욕 진행 시간
    g_timeVal = millis() - g_startTime;  // 종료 시간 체크

    // 진행 시간 계산
    int usesec = (g_timeVal / 1000) % 60;
    int usemin = ((g_timeVal / 1000) / 60) % 60;
    int usehour = ((g_timeVal / 1000) / (60 * 60)) % 24;

    historyData.useTime = (g_timeVal / 1000);

    // 종료 시간 계산
    historyData.sec = historyData.sec + usesec;
    historyData.minute = historyData.minute + usemin;
    historyData.hour = historyData.hour + usehour;

    if (historyData.sec >= 60) {
      historyData.sec = historyData.sec - 60;
      historyData.minute++;
    }
    if (historyData.minute >= 60) {
      historyData.minute = historyData.minute - 60;
      historyData.hour++;
    }
    if (historyData.hour >= 24) {
      historyData.hour = historyData.hour - 24;
      historyData.day++;
    }

    if (historyData.month == 2) {
      if (!(historyData.year % 4) && (historyData.year % 100) || !(historyData.year % 400)) {
        if (historyData.day >= 30) {
          historyData.day = historyData.day - 29;
          historyData.month++;
        }
      } else {
        if (historyData.day >= 29) {
          historyData.day = historyData.day - 28;
          historyData.month++;
        }
      }
    } else if (historyData.month == 4 || historyData.month == 6 || historyData.month == 9 || historyData.month == 11) {
      if (historyData.day >= 31) {
        historyData.day = historyData.day - 30;
        historyData.month++;
      }
    } else if (historyData.month == 12) {
      if (historyData.day >= 32) {
        historyData.day = historyData.day - 31;
        historyData.month = 1;
        historyData.year++;
      }
    } else {
      if (historyData.day >= 32) {
        historyData.day = historyData.day - 31;
        historyData.month++;
      }
    }

    sprintf(historyData.endTime, "%d-%02d-%02dT%02d:%02d:%02d", historyData.year, historyData.month, historyData.day, historyData.hour, historyData.minute, historyData.sec);

    g_timeVal = 0;

    if (debug) {
      Serial.println("-------------------목욕 끝--------------------------");
      Serial.println(historyData.startTime);  // 목욕 시작 시간
      Serial.println(historyData.endTime);    // 목욕 끝난 시간
      Serial.println(historyData.useTime);    // 목욕 진행 시간
    }

    Serial1.print("history");

    //Json 형식으로 변경
    String payload = (String)g_bathID;
    payload += "/" + (String)g_userID;
    payload += "/" + (String)infoData.degree;
    payload += "/" + (String)infoData.waterlevel;
    payload += "/" + (String)historyData.useTime;
    payload += "/" + (String)historyData.startTime;
    payload += "/" + (String)historyData.endTime;
    payload += "/";  //종료 문자

    Serial1.println(payload);  //변경된 Json 값을 블루투스로 전송
    Serial.println(payload);

    // 장치 초기화
    devClear();
    if (debug == 1) {
      Serial.println("목욕이 종료됨");
    }
  } else {  // 정지 상태가 아니라면 info 보냄
    Serial1.print("info");

    //Json 형식으로 변경
    String payload = (String)g_bathID;
    payload += "/" + (String)g_userID;
    payload += "/" + (String)infoData.bathstate;
    payload += "/" + (String)infoData.degree;
    payload += "/" + (String)infoData.waterlevel;
    payload += "/" + (String)infoData.cap;
    payload += "/" + (String)infoData.hvalve;
    payload += "/" + (String)infoData.cvalve;
    payload += "/" + (String)infoData.svalve;
    payload += "/" + (String)infoData.record;
    payload += "/" + (String)infoData.heatrays;
    payload += "/" + (String)g_uid;
    payload += "/";  //종료 문자

    Serial1.println(payload);  //변경된 Json 값을 블루투스로 전송
    if (debug) {
      Serial.println(payload);
    }

    if (infoData.record != 0) {
      infoData.record = 0;
    }
  }
});

Task t_degreeDepthCal(2000, TASK_FOREVER, []() {  // 온도, 수위 측정 스케줄러
  if (infoData.bathstate == 2 || infoData.bathstate == 3) {
    if (degreeCal() > 0) {
      infoData.degree = degreeCal();
    }
    if (((g_bathLevel - depthCal()) / g_bathLevel * 100) > 0) {
      infoData.waterlevel = (g_bathLevel - depthCal()) / g_bathLevel * 100;
      Serial.println(depthCal());
    }
    g_uid = millis();

    if (debug) {
      Serial.print("현재온도 : ");
      Serial.println(infoData.degree);
      Serial.print("현재수위 : ");
      Serial.println(infoData.waterlevel);
    }
  }
});

Task t_main(1000, TASK_FOREVER, []() {  // 상태 업데이트, 기능 실행
  // 상태 업데이트
  if (infoData.bathstate == 0 && (historyData.year != controlData.year || historyData.month != controlData.month || historyData.day != controlData.day || historyData.hour != controlData.hour || historyData.minute != controlData.minute || historyData.sec != controlData.sec)) {
    if (debug == 1) {
      Serial.println("명령시작");
    }

    historyData.year = controlData.year;
    historyData.month = controlData.month;
    historyData.day = controlData.day;
    historyData.hour = controlData.hour;
    historyData.minute = controlData.minute;
    historyData.sec = controlData.sec;

    sprintf(historyData.startTime, "%d-%02d-%02dT%02d:%02d:%02d", historyData.year, historyData.month,
            historyData.day, historyData.hour, historyData.minute, historyData.sec);

    Serial.print("historyData.Date  : ");
    Serial.println(historyData.startTime);
  }

  // 기능 실행
  if (controlData.degree != 99) { // 정지 상태 체크
    if (controlData.waterlevel == 0 || controlData.degree == 0) {  // 세부기능 실행
      if (debug) {
        Serial.println("세부 기능 실행");
      }

      // 마개 토글 변경 체크 확인
      if (controlData.cap != g_preCap) {
        if (controlData.cap == 1) {
          g_sendCap = 1;
          infoData.record = 5;
          infoData.cap = 1;
          g_preCap = 1;
          g_uid = millis();
        }
        if (controlData.cap == 0) {
          g_sendCap = 0;
          infoData.record = 6;
          infoData.cap = 0;
          g_preCap = 0;
          g_uid = millis();
        }
      }

      // 온수 토글 변경 체크 확인
      if (controlData.hvalve != g_preHvalve) {
        if (controlData.hvalve == 1) {
          devOnHotvalve();
          infoData.record = 9;
          g_preHvalve = 1;
          g_uid = millis();
        }
        if (controlData.hvalve == 0) {
          devOffHotvalve();
          infoData.record = 10;
          g_preHvalve = 0;
          g_uid = millis();
        }
      }

      // 냉수 토글 변경 체크 확인
      if (controlData.cvalve != g_preCvalve) {
        if (controlData.cvalve == 1) {
          devOnColdvalve();
          infoData.record = 7;
          g_preCvalve = 1;
          g_uid = millis();
        }
        if (controlData.cvalve == 0) {
          devOffColdvalve();
          infoData.record = 8;
          g_preCvalve = 0;
          g_uid = millis();
        }
      }

      // 청소 토글 변경 체크 확인
      if (controlData.cleantime != 0) {
        if (g_preCleantime == -9999) {
          g_preCleantime = controlData.cleantime;
          g_sendCap = 1;
          devOnSpkler();
          infoData.record = 3;
          g_preCap = 1;
          g_preSvalve = 1;
          g_uid = millis();
        }

        if (g_preCleantime != 0) {
          g_preCleantime--;
        }

        if (g_preCleantime == 0) {
          devOffSpkler();
          infoData.record = 4;
          g_preSvalve = 0;
          g_uid = millis();
        }
      }
    } else {  // 메인 목욕 기능 실행
      if (debug) {
        Serial.println("메인 목욕 실행");
      }

      if (infoData.bathstate == 0) {  // 실행 전 세팅
        infoData.record = 12;
        devClear();  //장치 초기화
        g_stopState = 0;
        g_startTime = millis();
        g_uid = millis();
        infoData.bathstate = 1;
      }

      if (infoData.bathstate == 1) {  // 메인 청소 동작
        if (g_preCleantime == -9999) {
          g_preCleantime = controlData.cleantime;
          g_sendCap = 1;
          devOnSpkler();
          g_preCap = 1;
          g_preSvalve = 1;
          g_uid = millis();
        }

        if (g_preCleantime != 0) {
          g_preCleantime--;
        }

        if (g_preCleantime == 0) {
          devOffSpkler();
          g_preSvalve = 0;
          g_uid = millis();
          infoData.bathstate = 2;
        }
      }

      if (infoData.bathstate == 2) {  // 메인 물 받기 동작
        if (infoData.waterlevel < controlData.waterlevel) {
          g_sendCap = 0;
          g_preCap = 0;
          if (infoData.degree >= controlData.degree + 3) {  //온도센서가 목표 온도보다 3도 높으면 냉수밸브만
            devOnColdvalve();
            devOffHotvalve();
          } else if (infoData.degree <= controlData.degree - 3) {  //온도센서가 목표 온도보다 3도 낮으면 온수만
            devOffColdvalve();
            devOnHotvalve();
          } else {  // 아니면 둘다
            devOnColdvalve();
            devOnHotvalve();
          }
        } else {
          devOffColdvalve();
          devOffHotvalve();
          g_uid = millis();
          infoData.bathstate = 3;
          infoData.record = 13;
        }
      }

      if (infoData.bathstate == 3) {                 // 메인 물 유지 동작
        if (infoData.degree < controlData.degree) {  //온도센서가 목표 온도보다 낮으면 열선 온
          devOnHeatrays();
        } else {  //높거나 같으면 열선 끔
          devOffHeatrays();
        }
      }
    }
  }

});

//////////////////////////////////////////
void setup() {
  // 블루투스 통신을 위한 시리얼 설정
  Serial.begin(9600);
  Serial1.begin(9600);
  Serial2.begin(9600);

  //아두이노 핀 출력/입력 설정
  pinMode(HEATRAYS, OUTPUT);
  pinMode(CVALVE, OUTPUT);
  pinMode(HVALVE, OUTPUT);
  pinMode(SVALVE, OUTPUT);
  pinMode(TEMPSENCOR, INPUT);
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);

  //장치 초기화
  devClear();

  // 욕조 높이 계산 이부분 수정 필요 첫 측정 depthcal로 변경 필요
  g_bathLevel = 7;
  if (debug) {
    Serial.println(g_bathLevel);
  }

  // 쓰레드 생성
  runner.init();
  runner.addTask(t_devCapSend);
  runner.addTask(t_devCapRecv);
  runner.addTask(t_devBtControl);
  runner.addTask(t_devBtInfo);
  runner.addTask(t_degreeDepthCal);
  runner.addTask(t_main);

  // 쓰레드 동작
  t_devCapSend.enable();
  t_devCapRecv.enable();
  t_devBtControl.enable();
  t_devBtInfo.enable();
  t_degreeDepthCal.enable();
  t_main.enable();
}

void loop() {
  runner.execute();
}

// 장치 초기화 함수
void devClear() {
  infoData.degree = 0;      // 현재 온도
  infoData.waterlevel = 0;  // 현재 수위(퍼센트)
  g_sendCap = 1;
  g_preCleantime = -9999;

  devOffHotvalve();
  devOffColdvalve();
  devOffSpkler();
  devOffHeatrays();

  g_preCap = 1;
  g_preHvalve = 0;
  g_preCvalve = 0;
  g_preSvalve = 0;
  g_preDegree = 0;
  g_uid = millis();
}

//온도 측정 함수
float degreeCal() {
  byte data[12];
  byte addr[8];
  if (!ds.search(addr)) {
    ds.reset_search();
    return -1000;
  }
  if (OneWire::crc8(addr, 7) != addr[7]) {
    Serial.println("CRC is not valid");
    return -1000;
  }
  if (addr[0] != 0x10 && addr[0] != 0x28) {
    Serial.print("Device is not recognized");
    return -1000;
  }
  ds.reset();
  ds.select(addr);
  ds.write(0x44, 1);
  byte present = ds.reset();
  ds.select(addr);
  ds.write(0xBE);

  for (int i = 0; i < 9; i++) {
    data[i] = ds.read();
  }

  ds.reset_search();
  byte MSB = data[1];
  byte LSB = data[0];
  float tempRead = ((MSB << 8) | LSB);
  float TemperatureSum = tempRead / 16;
  return TemperatureSum;
}

// 욕조 높이 측정 함수
float depthCal() {
  //trig핀에 사각펄스를 만든다.
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);

  float depth = (pulseIn(ECHO, HIGH) / 29.0) / 2;  //cm

  return depth;
}

// 기본 센서들 함수화
void devOnHotvalve() {
  digitalWrite(HVALVE, HIGH);
  infoData.hvalve = 1;
}

void devOffHotvalve() {
  digitalWrite(HVALVE, LOW);
  infoData.hvalve = 0;
}

void devOnColdvalve() {
  digitalWrite(CVALVE, HIGH);
  infoData.cvalve = 1;
}

void devOffColdvalve() {
  digitalWrite(CVALVE, LOW);
  infoData.cvalve = 0;
}

void devOnSpkler() {
  digitalWrite(SVALVE, HIGH);
  infoData.svalve = 1;
}

void devOffSpkler() {
  digitalWrite(SVALVE, LOW);
  infoData.svalve = 0;
}

void devOnHeatrays() {
  digitalWrite(HEATRAYS, HIGH);
  infoData.heatrays = 1;
}

void devOffHeatrays() {
  digitalWrite(HEATRAYS, LOW);
  infoData.heatrays = 0;
}