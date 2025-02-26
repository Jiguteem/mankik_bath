<template>
  <div class="black-bg">
    <div class="white-bg">
      <div id="popup_content">
        <div id="time_Select">
          <VueScrollPicker
            :options="ampm"
            v-model="currentampm"
            @update:modelValue="this.setDate()"
          />
          <VueScrollPicker
            :options="hour"
            v-model="currenthour"
            @update:modelValue="this.setDate()"
          />
          <VueScrollPicker
            :options="minute"
            v-model="currentminute"
            @update:modelValue="this.setDate()"
          />
        </div>

        <div id="popup_content_detail">
          <!--캘린더-->
          <div id="calender_div">
            <div id="calender_btn" v-on:click="openCalender">
              <div>캘린더</div>
              <div>
                <span id="dateText">{{ dateText }}</span>
                <font-awesome-icon icon="chevron-down" />
              </div>
            </div>
            <VDatePicker
              id="calender"
              v-if="isCalanderopen"
              v-model="date"
              :dayclick="this.setCalendar()"
              expanded
              :min-date="new Date()"
            />
            <div id="warningText">{{ warningText }}</div>
          </div>
          <hr />
          <!--온도-->
          <div id="temp_div">
            <div>온도</div>
            <VueSlider
              v-model="temp"
              :min="30"
              :max="40"
              :interval="0.5"
              :contained="true"
              :adsorb="true"
              :marks="tempmarks"
            />
          </div>
          <hr />

          <!--물 높이-->
          <div id="level_div">
            <div>욕조 물 높이</div>
            <VueSlider
              v-model="level"
              :min="25"
              :max="95"
              :interval="5"
              :contained="true"
              :adsorb="true"
              :marks="levelmarks"
            />
          </div>
        </div>
      </div>

      <div id="btn_div">
        <div id="btn_ok" v-on:click="sendReserve">확인</div>
        <div id="btn_cancle" v-on:click="offPopup">취소</div>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, ref } from "vue";
import VueSlider from "vue-slider-component";
import "vue-slider-component/theme/default.css";
import { VueScrollPicker } from "vue-scroll-picker";
import "vue-scroll-picker/lib/style.css";

export default defineComponent({
  components: {
    VueScrollPicker,
    VueSlider,
  },
  props: {
    allreserveArr: {},
    allreserveArrKey: {},
    toast: {},
  },
  setup() {
    const temp = ref(36);
    const level = ref(50);
    const date = ref(new Date());

    var t = new Date();
    const currentampm = ref(t.getHours() >= 12 ? true : false);
    const currenthour = ref(t.getHours() % 12);
    const currentminute = ref(t.getMinutes() + 5);

    return {
      temp,
      level,
      date,
      currentampm,
      currenthour,
      currentminute,
    };
  },
  watch: {
    allreserveArr() {
      this.allArr = this.allreserveArr;
    },
    allreserveArrKey() {
      this.allArrKey = this.allreserveArrKey;
    },
  },
  data() {
    return {
      allArr: this.allreserveArr,
      allArrKey: this.allreserveArrKey,
      isCalanderopen: false,
      todaydate: new Date(),
      reserveTime: "",
      isOtherReserve: false,
      levelmarks: [25, 50, 75, 95],
      tempmarks: [30, 32, 34, 36, 38, 40],
      ampm: [
        { value: false, name: "오전" },
        { value: true, name: "오후" },
      ],
      hour: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
      minute: [
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
        38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, 58, 59,
      ],
    };
  },
  computed: {
    dateText() {
      if (this.date == null) {
        return "";
      }
      if (
        this.todaydate.toLocaleDateString() === this.date.toLocaleDateString()
      ) {
        return "오늘";
      }
      return this.date.toLocaleDateString();
    },
    warningText() {
      if (this.date == null) {
        return "날짜를 선택해주세요";
      }
      if (this.isOtherReserve == false) {
        return "";
      }
      return "30분 내에 겹치는 예약이 있어 목욕이 지연될 수 있습니다.";
    },
  },
  methods: {
    openCalender() {
      if (this.isCalanderopen == false) {
        this.isCalanderopen = true;
      } else {
        this.isCalanderopen = false;
      }
    },
    setCalendar() {
      this.setDate();
    },
    setDate() {
      if (this.date != null) {
        var year = this.date.getFullYear();
        var month = ("0" + (this.date.getMonth() + 1)).slice(-2);
        var day = ("0" + this.date.getDate()).slice(-2);
        var dateString = year + "-" + month + "-" + day + "T";
        var hours;
        if (this.currentampm == false) {
          hours = ("0" + (this.currenthour % 12)).slice(-2);
        } else {
          hours = ("0" + ((this.currenthour % 12) + 12)).slice(-2);
        }

        var minutes = ("0" + this.currentminute).slice(-2);
        var seconds = "00";
        var timeString = hours + ":" + minutes + ":" + seconds;
        this.reserveTime = dateString + timeString;

        //30분 내에 다른 예약이 있는지 확인
        for (let a = this.allArrKey.length - 1; a >= 0; a--) {
          var otherd = new Date(this.allArr[this.allArrKey[a]].starttime);
          var myd = new Date(this.reserveTime);
          var timediff = Math.abs(otherd.getTime() - myd.getTime());

          if (timediff <= 1800000) {
            //시간 차이가 30분보다 작으면
            this.isOtherReserve = true;
            break;
          }
          this.isOtherReserve = false;
        }
      } else {
        this.reserveTime = null;
      }
    },
    sendReserve() {
      if (new Date(this.reserveTime).getTime() > new Date().getTime() + 50000) {
        this.$emit("sendReserve", this.temp, this.level, this.reserveTime);
      } else {
        this.toast.error("과거나 너무 가까운 시간은 예약할 수 없어요.");
      }
      this.$emit("offPopup");
    },
    offPopup() {
      this.$emit("offPopup");
    },
  },
});
</script>
<style scoped>
body {
  margin: 0;
}
.black-bg {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  position: fixed;
  padding-top: 5%;
}

.white-bg {
  width: 90%;
  margin: 0 auto;
  background: rgb(250, 250, 250);
  border-radius: 8px;
  overflow: auto;
}
@media (min-width: 1024px) {
  .black-bg {
    padding: 1%;
  }
  .white-bg {
    width: 60%;
  }
}
#popup_content {
}
#popup_content_detail {
  background-color: white;
  padding: 5%;
  height: 15%;
  overflow-y: scroll;
  overflow-x: hidden;
  border-radius: 12px;
  font-weight: 550;
}

#time_Select {
  display: flex;
  padding: 10%;
}
#calender_div {
}
#calender_div #calender_btn {
  display: flex;
  justify-content: space-between;
}
#calender_div #dateText {
  margin-right: 5px;
}

#calender_div #warningText {
  font-size: 8px;
  font-weight: bold;
  text-align: center;
  color: red;
}

#temp_div {
  margin-bottom: 24px;
}

#level_div {
}

#btn_div {
  display: flex;
  justify-content: space-between;
  text-align: center;
  padding: 3%;
  font-weight: bold;
}
#btn_div #btn_ok {
  width: 49.5%;
}
#btn_div #btn_cancle {
  width: 49.5%;
}
hr {
  background: rgba(0, 0, 0, 0.226);
  height: 1px;
  border: 0;
}
</style>
<style>
.vue-scroll-picker-item {
  line-height: 2em;
  color: rgb(141 141 141 / 70%);
}
.vue-scroll-picker-item-selected {
  color: #007bff;
}
.vue-scroll-picker-layer-top {
  background: transparent;
}
.vue-scroll-picker-layer-bottom {
  background: transparent;
}
</style>