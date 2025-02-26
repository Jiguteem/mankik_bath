<template>
  <div>
    <!--물온도 박스-->
    <div id="temp_box" class="box">
      <div class="box_title">물 온도</div>
      <div id="temp-text">{{ temp }}<span class="text-sign">°C</span></div>
      <VueSlider
        id="temp_slider"
        v-model="temp"
        :min="30"
        :max="40"
        :interval="0.5"
        :contained="true"
        :adsorb="true"
        :marks="tempmarks"
        @drag-end="onchangeTemp"
        @click="onchangeTemp"
      />
      <div style="height: 1px"></div>
    </div>
    <!--높이 조절-->

    <!--냉수 밸브-->
    <!--온수 밸브-->
    <div class="boxcontainer">
      <div id="waterlevel_box" class="box half">
        <div class="box_title">높이 조절</div>
        <div id="level-content">
          <div>
            <VueSlider
              id="level_slider"
              v-model="level"
              direction="btt"
              :min="25"
              :max="95"
              :interval="5"
              :contained="true"
              :adsorb="true"
              :marks="levelmarks"
              :tooltip-placement="'right'"
              style="height: 100px"
              @drag-end="onchangeLevel"
              @click="onchangeLevel"
            />
          </div>
          <div id="level-text">{{ level }}<span class="text-sign">%</span></div>
        </div>
      </div>
      <div class="half">
        <div id="cvalve_box" class="box half_top" v-on:click="btnColdValve">
          <div class="box_title">
            {{ bathState["c_valve"] ? "냉수 밸브 닫기" : "냉수 밸브 열기" }}
          </div>
          <img src="../../src/assets/img/cap_close.png" alt="" />
        </div>

        <div id="hvalve_box" class="box" v-on:click="btnHotValve">
          <div class="box_title">
            {{ bathState["h_valve"] ? "온수 밸브 닫기" : "온수 밸브 열기" }}
          </div>
          <img src="../../src/assets/img/cap_close.png" alt="" />
        </div>
      </div>
    </div>
    <!--욕조 청소-->
    <!--마개 버튼-->
    <div class="boxcontainer">
      <div id="clean_box" class="box half" v-on:click="btnBathClean">
        <div class="box_title">욕조 청소</div>
        <img src="../../src/assets/img/clean.png" alt="" />
      </div>

      <div id="cap_box" class="box half" v-on:click="btnCap">
        <div class="box_title">
          {{ bathState["cap"] ? "마개 닫기" : "마개 열기" }}
        </div>
        <img src="../../src/assets/img/cap_close.png" alt="" />
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import VueSlider from "vue-slider-component";
import "vue-slider-component/theme/default.css";

export default defineComponent({
  components() {
    VueSlider;
  },
  setup() {
    const temp = ref(36);
    const level = ref(50);

    return {
      temp,
      level,
    };
  },
  data() {
    return {
      coldValvetext: "냉수 밸브 열기",
      hotValvetext: "온수 밸브 열기",
      captext: "마개 열림",
      levelmarks: [25, 50, 75, 95],
      tempmarks: [30, 32, 34, 36, 38, 40],
    };
  },
  props: {
    bathState: {
      type: Object,
      default: () => {
        return {};
      },
    },
    userArray: {
      type: Array,
      default: undefined,
    },
    toast: {},
    bathid: {},
    userid: {},
  },
  methods: {
    //냉수 밸브 열기/닫기
    btnColdValve() {
      var orderc_valve = 0;
      var toastmsg = "냉수 밸브를 닫습니다.";

      if (this.bathState.c_valve == 0) {
        orderc_valve = 1;
        toastmsg = "냉수 밸브를 엽니다.";
      }

      this.$http
        .post("control/" + this.bathid, {
          cap: this.bathState.cap,
          h_valve: this.bathState.h_valve,
          c_valve: orderc_valve,
          cleantime: this.bathState.clean_valve,
          user_id: 1,
          temp: 0,
          level: 0,
        })
        .then((res) => {
          console.log(res);
          this.toast(toastmsg);
          this.$emit("repeatRefresh");
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //온수 밸브 열기/닫기
    btnHotValve() {
      var orderh_valve = 0;
      var toastmsg = "온수 밸브를 닫습니다.";

      if (this.bathState.h_valve == 0) {
        orderh_valve = 1;
        toastmsg = "온수 밸브를 엽니다.";
      }

      this.$http
        .post("control/" + this.bathid, {
          cap: this.bathState.cap,
          h_valve: orderh_valve,
          c_valve: this.bathState.c_valve,
          cleantime: this.bathState.clean_valve,
          user_id: 1,
          temp: 0,
          level: 0,
        })
        .then((res) => {
          console.log(res);
          this.toast(toastmsg);
          this.$emit("repeatRefresh");
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //청소 명령
    btnBathClean() {
      this.$http
        .post("control/" + this.bathid, {
          cap: this.bathState.cap,
          h_valve: this.bathState.h_valve,
          c_valve: this.bathState.c_valve,
          clean: 15,
          user_id: 1,
          temp: 0,
          level: 0,
        })
        .then((res) => {
          console.log(res);
          this.toast("욕조 청소가 시작됩니다.");

          this.$emit("repeatRefresh");
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },
    //마개 열기 닫기
    btnCap() {
      var ordercap = 0;
      var toastmsg = "마개를 닫습니다.";

      if (this.bathState.cap == 0) {
        ordercap = 1;
        toastmsg = "마개를 엽니다.";
      }

      this.$http
        .post("control/" + this.bathid, {
          cap: ordercap,
          h_valve: this.bathState.h_valve,
          c_valve: this.bathState.c_valve,
          cleantime: this.bathState.clean_valve,
          user_id: 1,
          temp: 0,
          level: 0,
        })
        .then((res) => {
          console.log(res);
          this.toast(toastmsg);

          this.$emit("repeatRefresh");
        })
        .catch((err) => {
          console.error(err);
          this.toast.error("오류가 발생하였습니다.\n잠시후 다시 시도해주세요.");
        });
    },

    //App.vue에게 목표 온도값 전달
    onchangeTemp(e) {
      this.$emit("inputTemp", this.temp);
    },
    //App.vue에게 목표 수위값 전달
    onchangeLevel(e) {
      this.$emit("inputLevel", this.level);
    },
  },
});
</script>
<style scoped>
.box {
  background-color: white;
  text-align: start;
  border: white;
  border-radius: 10px;
  box-shadow: 1px 1px 0px 0px;
  width: 100%;
}
.boxcontainer {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  align-items: stretch;
  margin: 10px 0px;
}
.half {
  width: 49%;
}
.half_top {
  margin-bottom: 10px;
}
.box_title {
  font-weight: bold;
  font-size: 14px;
  padding: 8px;
}

#level-content {
  display: flex;
  justify-content: space-between;
  margin: 5px;
}

#temp_slider {
  margin-bottom: 13px;
}

#temp-text {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
}

#level-text {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  line-height: 100px;
}

.text-sign {
  font-size: 16px;
  margin-left: 2px;
}
</style>
<style>
.vue-slider-ltr .vue-slider-mark-label,
.vue-slider-rtl .vue-slider-mark-label {
  margin-top: 5px;
  font-size: 12px;
}
</style>