<template>
  <div class="aside">
    <div class="aside-content">
      <el-menu
        default-active="1"
        class="el-menu-vertical-demo"
        :collapse="isCollapse"
        @open="handleOpen"
        @close="handleClose"
        :style="{ backgroundColor: asideColor }"
      >
        <el-menu-item index="0" class="aside-top">
          <template #default>
            <div style="display: flex">
              <template v-if="!isCollapse">
                <div class="aside-top-info-profile" style="width: 36px">
                  <div style="padding-top: 10px">
                    <div class="current-workspace-icon">T</div>
                  </div>
                </div>
                <div
                  class="aside-top-info"
                  style="width: 200px; padding-left: 5px"
                >
                  <span>Trello</span>
                  <span>Free</span>
                </div>
              </template>
              <div class="aside-top-button" style="width: 24px">
                <div
                  class="aside-top-info-comp"
                  v-if="!isCollapse"
                  @click="handleClose"
                >
                  <i class="fas fa-chevron-left"></i>
                </div>
                <div class="aside-top-info-comp" v-else @click="handleOpen">
                  <i class="fas fa-chevron-circle-right"></i>
                </div>
              </div>
            </div>
          </template>
        </el-menu-item>
        <router-link to="/board/Board">
          <el-menu-item index="1">
            <i class="fab fa-trello icon"></i>
            <template #title>
              <span class="text"> Boards </span>
            </template>
          </el-menu-item>
        </router-link>
        <router-link :to="memberLink">
          <el-menu-item index="2">
            <i class="far fa-user icon"></i>
            <template #title>
              <span class="text">Members</span>
            </template>
          </el-menu-item>
        </router-link>
        <el-menu-item index="3">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              <i class="fas fa-cog icon"></i>
              Workspace settings
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>Workspace settings</el-dropdown-item>
              <el-dropdown-item>Upgrade workspace</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>

        <div class="menu-title" v-if="!isCollapse">Workspace views</div>
        <el-menu-item index="4">
          <i class="fas fa-th" aria-hidden="true"></i>
          <template #title>
            <span class="text">Table</span>
          </template>
        </el-menu-item>
        <el-menu-item index="5">
          <i class="far fa-calendar-alt"></i>
          <template #title>
            <span class="text">Calendar</span>
          </template>
        </el-menu-item>
        <div class="menu-title" v-if="!isCollapse">
          <h2 style="font-size: 13px">Your boards</h2>
          <div class="current-workspace">
            <div class="current-workspace-icon">T</div>
            <p>trello</p>
          </div>
        </div>
      </el-menu>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    asideColor: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      isCollapse: false,
    };
  },
  computed: {
    memberLink() {
      if (typeof localStorage === "undefined") {
        return "/login/Login";
      }
      return localStorage.getItem("accessToken")
        ? "/member/Profile"
        : "/login/Login";
    },
  },
  methods: {
    handleOpen() {
      if (this.isCollapse) {
        this.isCollapse = false;
        // $emit false 전달
        this.$emit("collActive", false);
      }
    },
    handleClose() {
      if (!this.isCollapse) {
        this.isCollapse = true;
        // $emit true 전달
        this.$emit("collActive", true);
      }
    },
  },
};
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  border-right: 0.1px solid rgb(209, 209, 209);
  width: 95%;
  height: 700px;
  transition: all 0.05s;
  /* background-color: #ffd8d8; */
}
.el-menu--collapse {
  height: 700px;
  transition: all 0.05s;
}
.icon {
  margin-right: 10px;
}
.text {
  margin-left: 10px;
}
.aside {
  /* background-color: #0079bf; */
}
.aside-top {
  height: 70px;
  line-height: 70px;
  border-bottom: 0.1px solid rgb(209, 209, 209);
}
.aside-top-info {
  width: 200px;
  height: 70px;
  padding-left: 5px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.aside-top-info > span {
  height: 20px;
  line-height: 10px;
}
.info-profile {
  width: 36px;
  height: 36px;
  border-radius: 10px;
}
.menu-title {
  margin-top: 20px;
  padding-left: 20px;
  overflow: hidden;
}
a {
  text-decoration: none;
  color: black;
}
.current-workspace {
  display: flex;
  align-items: center;
  width: 300px;
}
.current-workspace-icon {
  width: 36px;
  height: 36px;
  background-color: #0079bf;
  color: white;
  /* border-radius: 50%; */
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  margin-right: 10px;
}
</style>
