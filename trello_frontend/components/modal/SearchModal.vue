<template lang="">
  <div>
    <el-dialog
      width="800px"
      :visible.sync="show"
      @close="$emit('close')"
      style="color: black"
    >
      <div>
        <template>
          <el-input
            v-model="keyword"
            placeholder="Search"
            clearable
            @keyup.enter.native="getSearch(keyword)"
          />
        </template>
        <div>
          <div>
            <div class="result-card-title">
              <span>CARDS</span>
            </div>
            <div
              class="result-content"
              v-for="(rowData, idx) in uniqueResultList"
              :key="idx"
              v-if="rowData.title !== null && rowData.title.startsWith(keyword)"
            >
              <div
                class="result-cards"
                @click="handleCardClick(rowData.cardCode, rowData.listCode)"
              >
                <i class="fas fa-credit-card" style="color: #5c5c5c"></i>
                <span style="margin-left: 5px">
                  {{ rowData.title }}
                </span>
              </div>
            </div>
          </div>
          <span class="result-activ-title">ACTIVITIES</span>
          <div
            class="result-content"
            v-for="(rowData, idx) in resultList"
            :key="idx"
            v-if="
              rowData.content !== null && rowData.content.startsWith(keyword)
            "
          >
            <div
              class="result-activ"
              @click="handleCardClick(rowData.cardCode, rowData.listCode)"
            >
              <i class="fas fa-list" style="color: #5c5c5c"></i>
              <span style="margin-left: 5px">
                {{ rowData.content }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { EventBus } from "~/common/eventBus.js";

export default {
  data() {
    return {
      // 경고 메세지 때문에 변수 할당
      showModal: this.show,
      keyword: "",
      resultList: [],
    };
  },
  computed: {
    // content 존재 computed
    isFormContentValid() {
      return this.form.content !== "" && this.form.content !== null;
    },
    // 리스트는 중복 처리 X
    uniqueResultList() {
      return this.resultList.reduce((acc, cur) => {
        // 현재 title이랑 같은지
        if (!acc.find((item) => item.title === cur.title)) {
          acc.push(cur);
        }
        return acc;
      }, []);
    },
  },
  props: ["show"],
  watch: {
    show() {
      if (this.show == true) {
        console.log("open");
      } else {
        this.$emit("close");
      }
    },
  },
  methods: {
    async getSearch(text) {
      console.log(text);
      await this.$axios
        .get("/card/getResultBySearch?keyword=" + text)
        .then((res) => {
          console.log(res);
          this.resultList = res.data;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // 이벤트버스 이용해서 비 부모/자식간 데이터 통신
    handleCardClick(cardCode, listCode) {
      EventBus.$emit("card-selected", { cardCode, listCode });
    },
  },
};
</script>
<style scoped>
.result-cards {
  height: 30px;
  line-height: 30px;
}
.result-cards:hover {
  background-color: #f0f0f0;
  cursor: pointer;
}
.result-activ {
  height: 30px;
  line-height: 30px;
}
.result-activ:hover {
  background-color: #f0f0f0;
  cursor: pointer;
}
.result-card-title {
  color: gray;
  font-weight: 700;
  font-size: 13px;
  margin-top: 5px;
  height: 40px;
}
.result-activ-title {
  color: gray;
  font-weight: 700;
  font-size: 13px;
  margin-top: 10px;
  height: 40px;
}
.result-content {
  margin-top: 0;
}
</style>
