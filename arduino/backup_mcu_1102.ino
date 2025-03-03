#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <ArduinoJson.h>
SoftwareSerial BtSerial(D2, D3);  // Rx,Tx

const char* ssid = "1111";
const char* password = "11111111";
char* host = "";
String BtString = "";

WiFiClient client;
HTTPClient http;

//String getArduinoSchedule();
String getArduinoControl();
String postArduinoUpdate_s(int bathid, int state, int temp, int waterlevel, int cap, int hvalve, int cvalve, int spkler, int fanonoff, int fanspeed, int heat, int ledonoff, int ledbright, int ledcolor);
String postArduinoHistory_s(int bathid, int userid, int temp, int level, int bath_time, String start_time, String end_time );
//String postArduinoUpdate(String s);
//String postArduinoHistory(String s);


void setup() {
  BtSerial.begin(9600);
  Serial.begin(9600);
  Serial.println("");
  Serial.print("Connect to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  // 연결중
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  //getArduinoControl();
  //getArduinoSchedule();
  //Serial.print(postArduinoHistory(1,1,"2023-05-11 09:13:33","2023-05-11 09:13:33",80,1));


  if (BtSerial.available()) {
    char btread;
    btread = BtSerial.read();
    BtString += btread;
    Serial.write(btread);
    //Serial.print(BtString);
    delay(20);  //수신 대기
  }

  if (Serial.available()) {
    BtSerial.write(Serial.read());
  }

  if (BtString.equals("control")) {
    String tmp_get = getArduinoControl();
    char* cc = (char*)tmp_get.c_str();  //형변환
    delay(100);
    BtSerial.print(cc);
    //delay(100);
    BtString = "";
  }

  if (BtString.equals("info")) {
    Serial.println("받음");
    //delay(100);

    String tmp_bt = "";
    int ti = 0;
    int a[14];

    while (BtSerial.available()) {
      char btread;
      btread = BtSerial.read();
      Serial.write(btread);
      if (btread == '/') {
        //aa[ti] = (char*)tmp_bt.c_str();
        a[ti] = tmp_bt.toInt();
        ti++;
        tmp_bt = "";
      } else {
        tmp_bt += btread;
      }
    }
    //delay(100);
    String tmp_get = postArduinoInfo_s(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11]);
    char* cc = (char*)tmp_get.c_str();  //형변환
    BtSerial.println(cc);
    BtString = "";
  }

  if (BtString.equals("history")) {
    Serial.println("받음");
    //delay(100);

    String tmp_bt = "";
    int ti = 0;
    int h[5];
    String s[2];

    while (BtSerial.available()) {
      char btread;
      btread = BtSerial.read();
      Serial.write(btread);
      if (btread == '/') {
        //aa[ti] = (char*)tmp_bt.c_str();
        if(ti == 5){
          s[0] = tmp_bt + ".000Z";
        }
        if(ti == 6){
          s[1] = tmp_bt + ".000Z";
        }
        h[ti] = tmp_bt.toInt();
        ti++;
        tmp_bt = "";
      } else {
        tmp_bt += btread;
      }
    }
    //delay(100);
    
    String tmp_get = postArduinoHistory_s(h[0], h[1], h[2], h[3], h[4], s[0], s[1]);
    char* cc = (char*)tmp_get.c_str();  //형변환
    BtSerial.println(cc);
    BtString = "";
  }

  if (BtString.length() > 21) {
    Serial.println("btstring");
    BtString = "";
  }
}
//---------------------------------------------------------------------------------------------------------
String getArduinoControl() { // 서버의 control 명령의 데이터를 읽어옴
  String tmp = host;
  String tmp_s = "control/1";
  tmp += tmp_s;
  String payload;

  Serial.printf("Connect to %s\n", tmp);
  http.begin(client, tmp);
  http.setTimeout(1000);
  int httpCode = http.GET();  //http 읽어오기(GET 사용)

  // post 다음과 같이 json 던지 면 됨
  // http.addHeader("Content-Type", "application/json");
  // int httpCode = http.POST("{\"value\":\"25\"}");

  //한개 이상 있으면 출력

  if (httpCode > 0) {
    Serial.printf("GET code : %d\n\n", httpCode);

    //ok (200) 이면 내용물 출력

    if (httpCode == HTTP_CODE_OK) {
      payload = http.getString();
      Serial.println(payload);
    }
  } else {
    //에러 출력
    Serial.printf("GET failed, error: %s\n", http.errorToString(httpCode).c_str());
  }
  http.end();

  //페이로드 파싱
  StaticJsonDocument<1000> doc;
  char* json = (char*)payload.c_str();
  /*char tmps[strlen(json) - 2];
  int j = 0;
  for (int i = 1; i < strlen(json) - 1; i++) {
   tmps[j++] = json[i];
  }
  tmps[j] = '\0';
  Serial.println(tmps); */
  Serial.println(json);
  auto error = deserializeJson(doc, json);
  if (error) {
    Serial.print(F("deserializeJson() failed with code "));
    Serial.println(error.c_str());
  }

  String bathid = doc["bathid"]["bathid"];
  String userid = doc["userid"]["userid"];
  String temp = doc["temp"];
  String waterlevel = doc["level"];
  String cap = doc["cap"];
  String hvalve = doc["h_valve"];
  String cvalve = doc["c_valve"];
  String cleantime = doc["cleantime"];
  String time = doc["time"];
  String time1 = time.substring(0, 19);
  Serial.println(bathid + "/" + userid + "/" + temp + "/" + waterlevel + "/" + cap + "/" + hvalve + "/" + cvalve + "/" + cleantime + "/" + time1);
  return bathid + "/" + userid + "/" + temp + "/" + waterlevel + "/" + cap + "/" + hvalve + "/" + cvalve + "/" + cleantime + "/" + time1;
}

// String getArduinoSchedule() { // 스케줄 예약 관련 일텐데 구현이 안됨
//   String tmp = host;
//   String tmp_s = "arduino/5948/schedule/1";
//   tmp += tmp_s;
//   String payload;

//   Serial.printf("Connect to %s\n", tmp);
//   http.begin(client, tmp);
//   http.setTimeout(1000);
//   int httpCode = http.GET();  //http 읽어오기(GET 사용)

//   // post 다음과 같이 json 던지 면 됨
//   // http.addHeader("Content-Type", "application/json");
//   // int httpCode = http.POST("{\"value\":\"25\"}");

//   //한개 이사 있으면 출력

//   if (httpCode > 0) {
//     Serial.printf("GET code : %d\n\n", httpCode);

//     //ok (200) 이면 내용물 출력

//     if (httpCode == HTTP_CODE_OK) {
//       payload = http.getString();
//       Serial.println(payload);
//     }
//   } else {
//     //에러 출력
//     Serial.printf("GET failed, error: %s\n", http.errorToString(httpCode).c_str());
//   }
//   http.end();
//   return payload;
// }

// String postArduinoInfo(String s) { // 초기에만든 블루투스로 받은 info데이터를 서버에 전송
//   String tmp = host;
//   String tmp_s = "info/1";
//   tmp += tmp_s;
//   String payload;

//   Serial.printf("Connect to %s\n", tmp);
//   http.begin(client, tmp);
//   http.setTimeout(1000);
//   //int httpCode = http.GET();//http 읽어오기(GET 사용)

//   //json 만들기
//   String json_tmp = s;


//   Serial.print(json_tmp);
//   // post 다음과 같이 json 던지 면 됨
//   http.addHeader("Content-Type", "application/json");
//   int httpCode = http.POST(json_tmp);

//   //한개 이사 있으면 출력

//   if (httpCode > 0) {
//     Serial.printf("post code : %d\n\n", httpCode);

//     //ok (200) 이면 내용물 출력

//     if (httpCode == HTTP_CODE_OK) {
//       payload = http.getString();
//       Serial.println(payload);
//     }
//   } else {
//     //에러 출력
//     Serial.printf("post failed, error: %s\n", http.errorToString(httpCode).c_str());
//   }
//   http.end();
//   return payload;
// }

// String postArduinoHistory(String s) {  // 초기에 만든 블루투스로 받은 info데이터를 서버에 전송
//   String tmp = host;
//   String tmp_s = "arduino/5948/history";
//   tmp += tmp_s;
//   String payload;

//   Serial.printf("Connect to %s\n", tmp);
//   http.begin(client, tmp);
//   http.setTimeout(1000);
//   //int httpCode = http.GET();//http 읽어오기(GET 사용)

//   //json 만들기
//   String json_tmp = s;

//   // post 다음과 같이 json 던지 면 됨
//   http.addHeader("Content-Type", "application/json");
//   int httpCode = http.POST(json_tmp);

//   //한개 이사 있으면 출력

//   if (httpCode > 0) {
//     Serial.printf("post code : %d\n\n", httpCode);

//     //ok (200) 이면 내용물 출력

//     if (httpCode == HTTP_CODE_OK) {
//       payload = http.getString();
//       Serial.println(payload);
//     }
//   } else {
//     //에러 출력
//     Serial.printf("post failed, error: %s\n", http.errorToString(httpCode).c_str());
//   }
//   http.end();
//   return payload;
// }

// 블루투스로 받은 info데이터를 서버에 전송
String postArduinoInfo_s(int bathid, int userid, int state, int temp, int waterlevel, int cap, int hvalve, int cvalve, int spkler, int fanonoff, int heat, unsigned int uid) {
  String tmp = host;
  String tmp_s = "info/" + String(bathid) + "/110"; /*String(userid);*/
  tmp += tmp_s;
  String payload;

  Serial.printf("Connect to %s\n", tmp);
  http.begin(client, tmp);
  http.setTimeout(1000);
  //int httpCode = http.GET();//http 읽어오기(GET 사용)

  //json 만들기
  String json_tmp = "{";

  json_tmp += "\"state\":\"";
  json_tmp += state;
  json_tmp += "\",";

  json_tmp += "\"temp\":\"";
  json_tmp += temp;
  json_tmp += "\",";

  json_tmp += "\"level\":\"";
  json_tmp += waterlevel;
  json_tmp += "\",";

  json_tmp += "\"cap\":\"";
  json_tmp += cap;
  json_tmp += "\",";

  json_tmp += "\"h_valve\":\"";
  json_tmp += hvalve;
  json_tmp += "\",";

  json_tmp += "\"c_valve\":\"";
  json_tmp += cvalve;
  json_tmp += "\",";

  json_tmp += "\"clean_valve\":\"";
  json_tmp += spkler;
  json_tmp += "\",";

  json_tmp += "\"fan\":\"";
  json_tmp += fanonoff;
  json_tmp += "\",";

  json_tmp += "\"heat\":\"";
  json_tmp += heat;
  json_tmp += "\",";

  json_tmp += "\"uid\":\"";
  json_tmp += uid;
  json_tmp += "\"}";

  Serial.print(json_tmp);
  // post 다음과 같이 json 던지 면 됨
  http.addHeader("Content-Type", "application/json");
  int httpCode = http.POST(json_tmp);

  //한개 이사 있으면 출력

  if (httpCode > 0) {
    Serial.printf("post code : %d\n\n", httpCode);

    //ok (200) 이면 내용물 출력

    if (httpCode == HTTP_CODE_OK) {
      //payload = http.getString();
      //Serial.println(payload);
    }
  } else {
    //에러 출력
    Serial.printf("post failed, error: %s\n", http.errorToString(httpCode).c_str());
  }
  http.end();
  return payload;
}

// 블루투스로 받은 history 데이터를 서버에 전송
String postArduinoHistory_s(int bathid, int userid, int temp, int level, int bath_time, String start_time, String end_time) {
  String tmp = host;
  String tmp_s = "history/" + String(bathid) + "/110"; /*String(userid);*/
  tmp += tmp_s;
  String payload;

  Serial.printf("Connect to %s\n", tmp);
  http.begin(client, tmp);
  http.setTimeout(1000);
  //int httpCode = http.GET();//http 읽어오기(GET 사용)

  //json 만들기
  String json_tmp = "{\"bathid\":\"";
  json_tmp += String(bathid);
  json_tmp += "\",";

  json_tmp += "\"userid\":\"";
  json_tmp += String(userid);
  json_tmp += "\",";

  json_tmp += "\"temp\":\"";
  json_tmp += temp;
  json_tmp += "\",";

  json_tmp += "\"level\":\"";
  json_tmp += level;
  json_tmp += "\",";

  json_tmp += "\"bath_time\":\"";
  json_tmp += bath_time;
  json_tmp += "\",";

  json_tmp += "\"start_time\":\"";
  json_tmp += start_time;
  json_tmp += "\",";

  json_tmp += "\"end_time\":\"";
  json_tmp += end_time;
  json_tmp += "\"}";

  // post 다음과 같이 json 던지 면 됨
  http.addHeader("Content-Type", "application/json");
  int httpCode = http.POST(json_tmp);

  //한개 이사 있으면 출력

  if (httpCode > 0) {
    Serial.printf("post code : %d\n\n", httpCode);

    //ok (200) 이면 내용물 출력

    if (httpCode == HTTP_CODE_OK) {
      payload = http.getString();
      Serial.println(payload);
    }
  } else {
    //에러 출력
    Serial.printf("post failed, error: %s\n", http.errorToString(httpCode).c_str());
  }
  http.end();
  return payload;
}