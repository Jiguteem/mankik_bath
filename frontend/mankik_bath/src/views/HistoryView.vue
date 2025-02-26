<template>
  <div>
    <VDatePicker
      :attributes="attributes"
      @did-move="this.getMonthHistory"
      @dayclick="this.getDateHistory"
      v-model="date"
      class="calendar"
      expanded
    />

    <div id="history_list">
      <h4 id="history_title">목욕 이력</h4>
      <!--목욕 이력 리스트-->
      <div id="list-content">
        <div v-for="(item, index) in historyArr" :key="index">
          <div class="historyItem">
            <div class="time-div">
              <span class="date-text">{{
                dateText(item[1]["start_time"])
              }}</span>
              <div>
                <span class="ampm-text">{{
                  ampmText(item[1]["start_time"].substr(11, 2))
                }}</span>
                <span class="time-text">{{
                  timeText(
                    item[1]["start_time"].substr(11, 2),
                    item[1]["start_time"].substr(14, 2)
                  )
                }}</span>
              </div>

              <!--목욕 시간-->
              <span
                ><font-awesome-icon icon="fa-clock" />
                <span class="minute-text">{{ item[1]["bath_time"] }}분</span>
              </span>
            </div>
            <div>
              <div class="flex-div">
                <font-awesome-icon
                  icon="temperature-three-quarters"
                  style="color: #fd4444"
                />
                <span class="temp-text">{{ item[1]["temp"] }}°C</span>
              </div>
              <div class="flex-div">
                <font-awesome-icon icon="water" style="color: #508ffb" />
                <span class="level-text">{{ item[1]["level"] }}%</span>
              </div>
            </div>
          </div>
          <hr />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent, ref } from "vue";

export default defineComponent({
  props: {
    userArr: { type: Array, default: undefined },
    userid: {},
  },
  setup() {
    const date = ref(new Date());
    const attributes = ref([
      {
        bar: "purple",
        dates: [],
      },
    ]);

    const todayyear = new Date().getFullYear();
    var arrDayStr = ["일", "월", "화", "수", "목", "금", "토"];

    return { attributes, todayyear, arrDayStr, date };
  },

  data() {
    return {
      hr: Array,
      historyArr: Object,
    };
  },
  methods: {
    getDateHistory() {
      this.$http
        .get("/history/" + this.userid, {
          params: {
            year: this.date.getFullYear(),
            month: this.date.getMonth() + 1,
            day: this.date.getDate(),
          },
        })
        .then((res) => {
          console.log(res.data);
          //응답: 목욕 시작시간, 목욕 종료 시간, 목욕 진행시간, 욕조 물 온도, 욕조 물 높이, 사용자 id, 기기 id
          this.historyArr = Object.entries(res.data).sort((a, b) =>
            b[1].start_time.localeCompare(a[1].start_time)
          );
          var i = 0;
          for (var item of Object.values(this.historyArr)) {
            this.hr[i] = item[1].start_time;
            i++;
          }
          this.attributes = { bar: "purple", dates: this.hr };
        })
        .catch((err) => {
          console.error(err);
        });
    },
    getMonthHistory(a) {
      this.$http
        .get("/history/" + this.userid, {
          params: {
            year: a[0].year,
            month: a[0].month,
          },
        })
        .then((res) => {
          console.log(res.data);
          //응답: 목욕 시작시간, 목욕 종료 시간, 목욕 진행시간, 욕조 물 온도, 욕조 물 높이, 사용자 id, 기기 id
          this.historyArr = Object.entries(res.data).sort((a, b) =>
            b[1].start_time.localeCompare(a[1].start_time)
          );
          var i = 0;
          for (var item of Object.values(this.historyArr)) {
            this.hr[i] = item[1].start_time;
            i++;
          }
          this.attributes = { bar: "purple", dates: this.hr };
        })
        .catch((err) => {
          console.error(err);
        });
    },

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
          year + "/" + month + "/" + day + "(" + this.arrDayStr[week] + ")"
        );
      } else {
        return month + "/" + day + "(" + this.arrDayStr[week] + ")";
      }
    },
  },
});
</script>
<style scoped>
#history_title {
  background-color: #006080;
  color: white;
  padding: 10px;
  border-radius: 8px;
  margin: 8px 0 0 0;
}

#list-content {
  padding: 8px;
  background-color: white;
  border-radius: 10px;
}
.historyItem {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.time-div {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-grow: 3;
}

.date-text {
  font-size: 16px;
  font-weight: bold;
}
.ampm-text {
  font-size: 10px;
  margin-right: 3px;
}
.time-text {
  font-size: 16px;
}

.minute-text {
  font-size: 16px;
  margin-right: 8px;
  margin-left: 3px;
}
.flex-div {
  display: flex;
  padding: 2px;
  align-items: center;
}
.temp-text {
  margin-left: 5px;
  font-size: 13px;
}
.level-text {
  margin-left: 5px;
  font-size: 13px;
}
</style>