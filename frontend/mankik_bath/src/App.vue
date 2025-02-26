<template>
  <!--예약 모달창-->
  <div>
    <ReservePopup
      v-if="isReservePopup == true"
      class="reserve-popup"
      @offPopup="offPopup"
      @sendReserve="sendReserve"
      :allreserveArr="this.allreserveArr"
      :allreserveArrKey="this.allreserveArrKey"
      :toast="this.toast"
    />
    <div id="appview">
      <!--상단바-->
      <div id="topbar">
        <div id="apptitle">
          <font-awesome-icon :icon="['fasr', 'arrow-left']" />
          스마트바스
        </div>
        <div id="topbar_menu">
          <font-awesome-icon :icon="['fas', 'bell']" style="color: #ff9900" />
          <ul id="user_profile">
            <li v-for="i in userArr" :key="i">
              <img :src="require(`@/assets/img/${i.img}.png`)" />
            </li>
          </ul>
        </div>
      </div>
      <!--목욕 시작 버튼 및 현재 온도-->
      <div id="bath_container">
        <div>
          <button id="bath_button" v-on:click="bathButtonClick">
            <font-awesome-icon
              v-bind:icon="bathbuttonIcon"
              :class="bathbuttonIcon_spin"
            />
            {{ bathbuttonText }}
          </button>
        </div>
        <div>
          <span id="curr_temp">{{ bathState.temp }}</span>
          <span id="temp_sign">°C</span>
        </div>
      </div>
      <!--욕조 이미지-->
      <div style="text-align: center">
        <img style="height: 250px" src="../src/assets/img/clean.png" />
      </div>
      <div id="bathState-text">{{ bathStateText }}</div>
      <div id="update_time">{{ updateTime }}</div>
      <font-awesome-icon
        icon="rotate-right"
        id="refresh-btn"
        v-on:click="repeatRefresh"
      />
      <!--탭 뷰-->
      <div id="tab">
        <div id="tabButton">
          <button v-on:click="currentTab = 'ControllerView'">컨트롤러</button>
          <button v-on:click="currentTab = 'ReserveView'">예약</button>
          <button v-on:click="currentTab = 'HistoryView'">이력</button>
        </div>
        <component
          v-bind:is="currentTabComponent"
          :userArr="userArr"
          :bathState="bathState"
          :toast="toast"
          @inputTemp="onChagebathTemp"
          @inputLevel="onChagebathLevel"
          :myreserveArr="myreserveArr"
          @getMyReserve="getMyReserve"
          @sendReserveDelete="sendReserveDelete"
          :bathid="this.bathid"
          :userid="this.userid"
          @onPopup="onPopup"
          @repeatRefresh="repeatRefresh"
        ></component>
      </div>
    </div>
  </div>
</template>

<script>
import ControllerView from "@/views/ControllerView.vue";
import HistoryView from "@/views/HistoryView.vue";
import ReserveView from "@/views/ReserveView.vue";
import ReservePopup from "./components/ReservePopup.vue";
import { useToast } from "vue-toastification";

//native와 메시지 교환
//webviewBridge.registerCallbackFunc(messageListener함수, partnerToken)

//백그라운드 비동기 함수
var refreshInterval;
var refreshCancel;
export default {
  name: "HomeView",
  components: {
    ControllerView,
    HistoryView,
    ReserveView,
    ReservePopup,
  },
  mounted() {
    this.getBathState();
    this.getReserveAllList();
    this.getMyReserve();
  },
  setup() {
    const toast = useToast();
    var historyArr = [];
    var allreserveArr = [];
    var allreserveArrKey = [];

    let bathid = 1;
    let userid = 1;

    return {
      toast,
      historyArr,
      allreserveArr,
      allreserveArrKey,
      bathid,
      userid,
    };
  },

  data() {
    return {
      //유저 정보 저장 배열(수정 필요)
      userArr: [{ img: "logo" }, { img: "cap_close" }, { img: "clean" }],
      bathbuttonIcon_spin: "", //빈칸 아니면 fa-spin
      currentTab: "ControllerView",
      isReservePopup: false,
      //목욕 온도, 수위
      bathTemp: 40,
      bathLevel: 30,
      myreserveArr: Object,

      //욕조 상태 새로고침한 시간
      updateTime: " ",

      //욕조 상태 변수(axios로 값 받아오기)
      bathState: {
        bathid: 1,
        state: 0, //욕조 명령 상태 (0 아무 동작안함, 1 목욕전청소, 2 물받기,  3 물 유지)
        temp: 0,
        level: 0,
        cap: 1,
        h_valve: 0,
        c_valve: 0,
        clean_valve: 0,
      },
    };
  },
  computed: {
    //인자나 http코드가 있으면 methods 써라
    currentTabComponent() {
      return this.currentTab;
    },
    bathbuttonText() {
      if (this.bathState.state == 0) {
        return "목욕 시작";
      } else {
        return "목욕 종료";
      }
    },
    bathbuttonIcon() {
      if (this.bathState.state == 0) {
        return "fa-play";
      } else {
        return "fa-stop";
      }
    },
    bathStateText() {
      switch (this.bathState.state) {
        case 0:
          return "목욕 명령을 기다리고 있어요";
        case 1:
          return "욕조 청소 중";
        case 2:
          return "물 받는 중(" + this.bathState.level + "%)";
        case 3:
          return "목욕 대기 중";
      }
    },
  },
  methods: {
    //주기적으로 애플리케이션 새로고치는 함수
    repeatRefresh() {
      this.getBathState();
      this.getReserveAllList();
      this.getMyReserve();
      
      if(refreshInterval){
        clearTimeout(refreshInterval);
        clearTimeout(refreshCancel);
      }

      refreshInterval = setInterval(() => {
        this.getBathState();
        this.getReserveAllList();
        this.getMyReserve();
      }, 3000);

      refreshCancel = setTimeout(function () {
        clearTimeout(refreshInterval);
      }, 10000);
    },

    //목욕시작, 목욕 종료 버튼 클릭 시
    bathButtonClick() {
      if (this.bathState.state == 0) {
        this.sendBathStart();
        console.log(this.bathTemp + "/" + this.bathLevel);
      } else {
        this.sendBathFinish();
      }
    },
    //axios 백엔드에 목욕 시작 명령 전송
    sendBathStart() {
      this.$http
        .post("control/" + this.bathid, {
          cap: 0,
          h_valve: 0,
          c_valve: 0,
          clean: 15,
          user_id: this.userid,
          temp: this.bathTemp, //슬라이더 값으로
          level: this.bathLevel, //슬라이더 값으로
        })
        .then((res) => {
          console.log(res);
          this.toast("목욕을 시작합니다.");
          this.repeatRefresh();
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //목욕 종료 명령 보내기
    sendBathFinish() {
      this.$http
        .post("control/" + this.bathid, {
          cap: 0,
          h_valve: 0,
          c_valve: 0,
          clean: 0,
          user_id: this.userid,
          temp: 99,
          level: 0,
        })
        .then((res) => {
          console.log("목욕 종료 메시지");
          console.log(res);
          this.toast("목욕을 종료합니다.");
          this.repeatRefresh();
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //해당 기기 모든 사용자의 예약 리스트 가져오기 http://13.125.231.37:8080/schedule/find/bath/1(bathid)
    getReserveAllList() {
      this.$http
        .get("schedule/find/bath/" + this.bathid, {}) //bathid
        .then((res) => {
          console.log(res);
          this.allreserveArr = res.data;
          this.allreserveArrKey = Object.keys(this.allreserveArr);
        })
        .catch((err) => {
          console.error(err);
        });
    },
    //자기 예약 리스트 가져오기 http://13.125.231.37:8080/schedule/find/user/(userid)
    getMyReserve() {
      this.$http
        .get("schedule/find/user/" + this.userid, {}) //userid
        .then((res) => {
          console.log(res);

          this.myreserveArr = Object.entries(res.data).sort((a, b) =>
            a[1].starttime.localeCompare(b[1].starttime)
          );
          console.log(this.myreserveArr);
        })
        .catch((err) => {
          console.error(err);
        });
    },
    //현재 욕조 상태 가져오는 함수
    getBathState() {
      var today = new Date();
      var year = today.getFullYear();
      var month = ("0" + (today.getMonth() + 1)).slice(-2);
      var day = ("0" + today.getDate()).slice(-2);
      var dateString = year + "-" + month + "-" + day;
      var hours = ("0" + today.getHours()).slice(-2);
      var minutes = ("0" + today.getMinutes()).slice(-2);
      var seconds = ("0" + today.getSeconds()).slice(-2);
      var timeString = hours + ":" + minutes + ":" + seconds;

      this.$http
        .get("/bath/" + this.bathid) //bathid
        .then((res) => {
          console.log(res.data);

          this.bathState.bathid = res.data.bathid;
          this.bathState.state = res.data.state;
          this.bathState.temp = res.data.temp;
          this.bathState.level = res.data.level;
          this.bathState.cap = res.data.cap;
          this.bathState.h_valve = res.data.h_valve;
          this.bathState.c_valve = res.data.c_valve;
          this.bathState.clean_valve = res.data.clean_valve;

          this.updateTime = dateString + " " + timeString + "에 불러옴";
        })
        .catch((err) => {
          console.error(err);
        });
    },
    //controllview에서 온도값 변경했을 시 수행하는 함수
    onChagebathTemp(val) {
      this.bathTemp = val;
    },
    //controllview에서 수위값 변경했을 시 수행하는 함수
    onChagebathLevel(val) {
      this.bathLevel = val;
    },
    //예약 명령
    sendReserve(popuptemp, popuplevel, time) {
      this.$http
        .post("schedule/" + this.bathid, {
          user_id: this.userid,
          temp: popuptemp,
          level: popuplevel,
          clean_time: 15,
          bath_start: time,
        })
        .then((res) => {
          console.log(res);

          this.toast("목욕이 예약되었습니다.");
          this.repeatRefresh();
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //예약 취소 명령
    sendReserveDelete(index) {
      this.$http
        .delete("schedule/delete/" + index)
        .then((res) => {
          this.toast("예약이 취소되었습니다.");
          this.repeatRefresh();
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //팝업 출력
    onPopup() {
      this.isReservePopup = true;
    },
    //팝업 종료
    offPopup() {
      this.isReservePopup = false;
    },
  },
};
</script>

<style>
body {
  background-color: #f1f8ff;
}

/* 가로 해상도가 768px 이상*/
@media (min-width: 768px) {
}

/* 가로 해상도가 1024px 이상*/
@media (min-width: 1024px) {
  #appview {
    width: 1024px;
    margin: 0 auto;
  }
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  background-color: #f1f8ff;
}
#appview {
  padding: 3%;
}

#topbar {
  color: #006080;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
#apptitle {
  font-size: 22px;
  font-weight: bold;
}
#topbar_menu {
  display: flex;
  align-items: center;
}
#update_time {
  text-align: center;
  color: gray;
  font-size: 12px;
}
#bathState-text {
  text-align: center;
  color: gray;
  font-size: 12px;
}

#user_profile {
  margin-right: 10px;
  padding-left: 10px;
  margin-left: 0;
  margin-top: 0;
  margin-bottom: 0;
  list-style-type: none;
}
#user_profile li {
  float: right;
  margin-right: -10px;
}
#user_profile img {
  width: 35px;
  height: 35px;
  border: 3px solid white;
  border-radius: 70%;
}

#bath_container {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  font-weight: bold;
}
#bath_button {
  border: 2px solid rgb(244, 146, 146);
  border-radius: 25px 25px 25px 25px;
  padding: 5px 10px;
  font-weight: bold;
  background-color: #f3f6f9;
}
#curr_temp {
  font-size: 40px;
}
#temp_sign {
  font-size: 20px;
}

#refresh-btn {
  border: 2px solid rgb(244, 146, 146);
  padding: 3px;
  border-radius: 25px;
  background-color: #f3f6f9;
}

#tab {
}

#tabButton {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
}

#tabButton button {
  width: 33%;
  height: 45px;
  border-radius: 10px;
  font-size: 17px;
  font-weight: bolder;
  background-color: #dfefff;
  border: none;
  color: #00c0ff;
  cursor:pointer;
}

#tabButton button:hover {
  background: #00bfff;
  color: white;
}
</style>
