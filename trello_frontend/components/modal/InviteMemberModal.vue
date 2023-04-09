<template lang="">
  <div>
    <el-dialog
      width="600px"
      :visible.sync="show"
      @close="$emit('close')"
      style="color: black"
      title="Share board"
    >
      <div class="share-email">
        <el-input
          v-model="userId"
          placeholder="Please enter User ID"
          clearable
          style="width: 450px"
        />
        <el-button
          type="primary"
          :style="{ backgroundColor: '#0079bf' }"
          @click="shareMember"
          >Share</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      // 경고 메세지 때문에 변수 할당
      showModal: this.show,
      keyword: "",
      userId: "",
      brdCode: "brd1",
    };
  },
  computed: {
    // content 존재 computed
    isFormContentValid() {
      return this.form.content !== "" && this.form.content !== null;
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
    shareMember() {
      console.log(this.userId);
      this.inviteMember();
    },

    async inviteMember() {
      let data = {
        brdCode: this.brdCode,
        userId: this.userId,
      };
      await this.$axios
        .post("/board/insertBoardMember", data)
        .then((res) => {
          alert(res.data.message);
          this.$router.go();
        })
        .catch((error) => {
          console.log(error);
          alert(error.response.data.message);
        });
    },
  },
};
</script>
<style scoped>
.share-email {
  display: flex;
  justify-content: space-between;
}
</style>
