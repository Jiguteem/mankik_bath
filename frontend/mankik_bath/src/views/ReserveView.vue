<template>
  <div>
    <div>
      <div id="btn_div">
        <font-awesome-icon
          :icon="['fas', 'plus']"
          id="plusBtn"
          v-on:click="onPopup"
        />
      </div>
      <div id="list-div">
        <div id="list-title">예약</div>
        <div id="empty-text" v-show="Object.keys(this.myArr).length === 0">
          {{ emptyText }}
        </div>
        <div v-for="(item, index) in myArr" :key="index" v-show="myArr">
          <div class="reserveItem">
            <div class="flex-div time-div">
              <div class="flex-div time-div-div">
                <span class="ampm-text">{{
                  ampmText(item[1].starttime.substr(11, 2))
                }}</span>
                <span class="time-text">{{
                  timeText(
                    item[1].starttime.substr(11, 2),
                    item[1].starttime.substr(14, 2)
                  )
                }}</span>
              </div>
              <span class="date-text">{{ dateText(item[1].starttime) }}</span>
            </div>

            <div class="delete-btn">
              <font-awesome-icon
                icon="xmark"
                v-on:click="sendReserveDelete(item[0])"
              />
            </div>
          </div>
          <hr />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  mounted() {},
  setup() {
    const todayyear = new Date().getFullYear();
    var arrDayStr = ["일", "월", "화", "수", "목", "금", "토"];

    return { todayyear, arrDayStr };
  },
  data() {
    return {
      myArr: this.myreserveArr,
    };
  },
  computed: {
    emptyText() {
      if (Object.keys(this.myArr).length !== 0) {
        return "";
      } else {
        return "예약이 비어있어요!";
      }
    },
  },
  methods: {
    ampmText(hour) {
      if (Number(hour) < 12) {
        return "오전";
      } else {
        return "오후";
      }
    },
    timeText(hour, minute) {
      var h = Number(hour);
      if (h > 12) {
        h -= 12;
      }
      return h + ":" + minute;
    },
    dateText(starttime) {
      var year = starttime.substr(0, 4);
      var month = starttime.substr(5, 2);
      var day = starttime.substr(8, 2);
      var week = new Date(starttime).getDay();
      if (year != this.todayyear) {
        return (
          year +
          "년 " +
          month +
          "월 " +
          day +
          "일 (" +
          this.arrDayStr[week] +
          ")"
        );
      } else {
        return month + "월 " + day + "일 (" + this.arrDayStr[week] + ")";
      }
    },
    onPopup() {
      this.$emit("onPopup");
    },
    getMyReserve() {
      this.$emit("getMyReserve");
    },
    sendReserveDelete(index) {
      this.$emit("sendReserveDelete", index);
    },
  },
  props: {
    myreserveArr: {},
    toast: {},
    bathid: {},
    userid: {},
  },
  watch: {
    //동적 props를 위해 사용
    myreserveArr() {
      this.myArr = this.myreserveArr;
    },
  },
};
</script>

<style scoped>
#btn_div {
  display: flex;
  flex-direction: row-reverse;

  margin: 10px 0;
}

#plusBtn {
  font-size: 24px;
}

#list-div {
  padding: 8px;
  background-color: white;
  border-radius: 10px;
}
#list-title {
  font-weight: bold;
  margin: 8px;
}

#empty-text {
  font-weight: bold;
  text-align: center;
  height: 180px;
  line-height: 180px;
}

.reserveItem {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
}
.flex-div {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.time-div {
  flex-grow: 2;
}

.ampm-text {
  font-size: 12px;
  margin-right: 6px;
}
.time-text {
  font-size: 24px;
}
.date-text {
  font-size: 12px;
  margin-right: 12px;
}
</style>