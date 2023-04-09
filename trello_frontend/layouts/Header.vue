<template lang="">
  <div class="header">
    <div class="header-title">
      <div style="margin-right: 10px">
        <i class="fas fa-grip-horizontal"></i>
      </div>
      <div class="item">
        <i class="fab fa-trello"></i>
        <router-link to="/">
          <span style="font-size: 20px; font-weight: 30px">Trello</span>
        </router-link>
      </div>
    </div>
    <div class="header-component">
      <div class="item-workspace">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            Workspaces<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="option1">
              <div class="workspace-dropdown">
                <h2 style="font-size: 13px">Current Workspace</h2>
                <div class="current-workspace">
                  <div class="current-workspace-icon">T</div>
                  <p>trello</p>
                </div>
                <h2 style="font-size: 13px">Your Workspaces</h2>
                <div class="current-workspace">
                  <div class="current-workspace-icon">T</div>
                  <p>trello</p>
                </div>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="item">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            Recent<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="option1">Option 1</el-dropdown-item>
            <el-dropdown-item command="option2">Option 2</el-dropdown-item>
            <el-dropdown-item command="option3">Option 3</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="item">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            Starred<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="option1">Option 1</el-dropdown-item>
            <el-dropdown-item command="option2">Option 2</el-dropdown-item>
            <el-dropdown-item command="option3">Option 3</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="item">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            Templates<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="option1">Option 1</el-dropdown-item>
            <el-dropdown-item command="option2">Option 2</el-dropdown-item>
            <el-dropdown-item command="option3">Option 3</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="item">
        <button class="btn-create">Create</button>
      </div>
    </div>
    <div class="header-blank"></div>
    <div class="header-profile">
      <div class="header-question-box" @click="showDetailModal">Q Search</div>
      <!-- <button
        @click="changeBackgroundColors"
        style="
          width: 50px;
          height: 30px;
          background-color: #0079bf;
          border: none;
        "
      >
        변경
      </button> -->
      <span class="noti">
        <i class="far fa-bell"></i>
      </span>
      <span class="info">
        <i class="far fa-question-circle"></i>
      </span>
      <el-dropdown trigger="click">
        <span class="account">
          <i class="fas fa-user-circle"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <!-- <el-dropdown-item command="option1"> -->
          <div class="account-dropdown">
            <div style="padding-left: 20px">
              <h4 style="font-size: 11px; color: gray">ACCOUNT</h4>
            </div>
            <div class="user-profile" @click="goProfile">
              <div class="user-profile-image">
                <img
                  src="/user.png"
                  alt="Profile Image"
                  class="profile-image"
                />
              </div>
              <div>
                <span style="font-size: 15px">{{ form.userName }}</span
                ><br />
                <span style="font-size: 12px; color: gray">{{
                  form.userEmail
                }}</span>
              </div>
            </div>
            <div class="account-dropdown-menu">
              <span
                class="account-dropdown-menu-item"
                style="font-size: 12px; font-weight: 1000; color: gray"
                >TRELLO</span
              >
              <span class="account-dropdown-menu-item"
                >Profile and visibility</span
              >
              <span class="account-dropdown-menu-item">Activity</span>
              <span class="account-dropdown-menu-item">Cards</span>
              <span class="account-dropdown-menu-item">Settings</span>
              <span class="account-dropdown-menu-item">Help</span>
              <span class="account-dropdown-menu-item">Shortcuts</span>
            </div>
            <div style="height: 40px; margin-top: 10px">
              <span class="item-logout" @click="userLogout" v-if="loginYn"
                >Log out</span
              >
            </div>
          </div>
          <!-- </el-dropdown-item> -->
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!-- 검색 모달 -->
    <search-modal :show="searchDetailModalShow" @close="closeDetailModal" />
  </div>
</template>
<script>
import SearchModal from "~/components/modal/SearchModal";
import Cookies from "js-cookie";
export default {
  components: { SearchModal },
  data() {
    return {
      form: {
        userName: "",
        userId: "",
        userEmail: "",
      },
      loginYn: false,
      searchDetailModalShow: false,
    };
  },
  methods: {
    // 회원 정보 불러오기
    async getMemberInfo() {
      try {
        if (
          typeof localStorage !== "undefined" &&
          localStorage.getItem("accessToken") != null
        ) {
          await this.$axios
            .get("/member/getMemberInfo", {
              headers: {
                Authorization: "Bearer " + localStorage.getItem("accessToken"),
              },
            })
            .then((res) => {
              this.form = res.data;
              this.loginYn = true;
              this.$emit("updateForm", this.form);
            })
            .catch((error) => {
              console.log("Error response:", error.response);
              console.log(error);
            });
        }
      } catch (error) {
        console.log(error);
      }
    },
    //유저로그아웃
    async userLogout() {
      try {
        const accessToken = localStorage.getItem("accessToken");
        if (accessToken) {
          const response = await this.$axios.post("/member/logout", {
            accessToken: accessToken,
          });

          if (response.status === 200) {
            localStorage.removeItem("accessToken");
            Cookies.remove("refreshToken");
            this.$router.push("/");
            setTimeout(() => {
              this.$router.go();
            }, 100);
          } else {
            console.error("로그아웃 에러:", response.status, response.data);
          }
        }
      } catch (error) {
        console.error("로그아웃 에러:", error);
      }
    },
    goProfile() {
      this.$router.push("/member/Profile");
    },
    showDetailModal() {
      this.searchDetailModalShow = true;
    },
    closeDetailModal() {
      this.searchDetailModalShow = false;
    },

    changeBackgroundColors() {
      const newCommonLayoutColor = "#fda7a7";
      const newHeaderColor = "#f66868";
      const newAsideColor = "#ffd8d8";

      this.$emit("updateBackgroundColors", {
        commonLayoutColor: newCommonLayoutColor,
        headerColor: newHeaderColor,
        asideColor: newAsideColor,
      });
    },
  },
  created() {
    this.getMemberInfo();
  },
};
</script>
<style scoped>
@import "@fortawesome/fontawesome-free/css/all.css";

.header {
  display: flex;
  justify-content: flex-start;
  line-height: 45px;
  flex-wrap: nowrap;
}
.item-workspace {
  flex-basis: 200px;
  text-align: center;
  /* flex-grow:1 */
}
.item {
  flex-basis: 120px;
  text-align: center;
  /* flex-grow:1 */
}
.block-col-2 .demonstration {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
.el-dropdown {
  color: #fff;
}
a {
  text-decoration: none;
  color: white;
}
.header-title {
  display: flex;
  justify-content: flex-start;
  width: 10%;
}
.header-component {
  display: flex;
  justify-content: flex-start;
  flex-basis: 600px;
}
.header-blank {
  flex-basis: 45%;
}
.header-profile {
  display: flex;
  justify-content: flex-start;
  width: 30%;
}
.noti {
  width: 32px;
  height: 45px;
  padding-left: 5px;
}
.info {
  width: 32px;
  height: 45px;
  padding-left: 5px;
}
.account {
  width: 32px;
  height: 45px;
  padding-left: 5px;
}
.account:hover {
  cursor: pointer;
}
.autocomplete {
  width: 70%;
}
.workspace-dropdown {
}
.current-workspace {
  display: flex;
  align-items: center;
  width: 300px;
}
.current-workspace-icon {
  width: 40px;
  height: 40px;
  background-color: #0079bf;
  color: white;
  /* border-radius: 50%; */
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  margin-right: 10px;
}
.btn-create {
  width: 65px;
  height: 32px;
  border-radius: 3px;
  background-color: #639ec4f4;
  color: white;
  border: none;
}
.user-profile {
  display: flex;
  height: 50px;
}
.user-profile:hover {
  cursor: pointer;
}
.user-profile-image {
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  padding-top: 5px;
}
.account-dropdown {
  width: 230px;
  height: 400px;
}
.account-dropdown-menu {
  display: flex;
  flex-direction: column;
  margin-top: 10px;
  border-bottom: 1px solid rgb(213, 212, 212);
}
.account-dropdown-menu-item {
  padding-left: 20px;
  height: 40px;
  font-size: 13px;
  font-weight: 400;
  line-height: 20px;
}
.account-dropdown-menu-item:hover {
  cursor: pointer;
}
hr {
  border: none;
  border-top: 1px solid gray;
  height: 10px;
}
.item-logout {
  padding-left: 20px;
  height: 40px;
  font-size: 14px;
  font-weight: 400;
  line-height: 20px;
}
.item-logout:hover {
  cursor: pointer;
}
.header-question-box {
  width: 200px;
  height: 30px;
  line-height: 30px;
  margin-top: 7px;
  margin-right: 7px;
  padding-left: 5px;
  border-radius: 5px;
  border: 1px solid #639ec4f4;
  background-color: #639ec4f4;
  font-weight: 300;
  font-size: 14px;
}
.header-question-box:hover {
  cursor: pointer;
}
</style>
