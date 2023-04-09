<template lang="">
  <div class="board-header">
    <div class="board-header-menu">
      <div class="board-title-header">
        <el-input
          v-model="brdData.brdName"
          :disabled="!isAdmin"
          class="w-50 m-2"
          size="large"
          placeholder="title"
          style="font-size: 15px"
          @blur="updateBrdInfo"
        />
      </div>
      <div class="board-header-star" @click="activateFavorite">
        <span v-if="starStatus == 0" class="favorite">
          <i class="far fa-star"></i>
        </span>
        <span v-else class="favorite">
          <i class="fas fa-star"></i>
        </span>
      </div>
      <el-dropdown trigger="click" class="board-works-visible" v-if="isAdmin">
        <span
          class="account"
          style="width: 100%; height: 100%; line-height: 25px"
        >
          <i :class="visibilityIcon"></i>
          <!-- <span>Workspace visible</span> -->
          <span>{{ visibilityText }}</span>
        </span>
        <el-dropdown-menu slot="dropdown">
          <div class="works-dropdown">
            <div class="works-dropdown-title">
              <span
                style="
                  font-size: 14px;
                  font-weight: 500;
                  color: gray;
                  text-align: center;
                "
              >
                Change visibility
              </span>
            </div>
            <div style="height: 200px">
              <ul class="visibility-list">
                <li class="visibility" @click="updateVisibility('00')">
                  <span>
                    <i class="fas fa-user-lock" style="color: #f73636"></i>
                    <span style="margin-left: 5px">Private</span></span
                  >
                  <span style="font-size: 10px"
                    >Only board members can see and edit this board</span
                  >
                </li>
                <li class="visibility" @click="updateVisibility('01')">
                  <span>
                    <i class="fas fa-user-friends" style="color: #080b12"></i
                    ><span style="margin-left: 5px">Workspace</span>
                  </span>
                  <span style="font-size: 10px"
                    >All members of the trello Workspace can see and edit this
                    board</span
                  >
                </li>
                <li class="visibility" @click="updateVisibility('10')">
                  <span>
                    <i class="fas fa-globe-asia" style="color: #58be4b"></i
                    ><span style="margin-left: 5px">Public</span>
                  </span>
                  <span style="font-size: 10px"
                    >Anyone on the internet can see this board.Only board
                    members can edit</span
                  >
                </li>
              </ul>
            </div>
          </div>
        </el-dropdown-menu>
      </el-dropdown>
      <div v-else class="board-works-non-visible" disabled>
        <span
          class="account"
          style="width: 100%; height: 100%; line-height: 25px"
        >
          <i :class="visibilityIcon"></i>
          <span>{{ visibilityText }}</span>
        </span>
      </div>
      <div class="board-type">
        <i class="fas fa-signal-alt-3"></i>
        <span style="font-size: 14px">Board</span>
      </div>
      <div class="board-type-select">
        <i class="fas fa-chevron-down"></i>
      </div>
    </div>
    <div class="board-header-menu">
      <div class="board-filter">
        <i class="fas fa-sort-amount-up-alt"></i>
        <span style="font-size: 14px">Filter</span>
      </div>
      <div class="board-user-profile">
        <template v-for="(rowData, idx) in adminList.slice(0, 4)">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              <img
                :src="'/user' + (idx + 1) + '.png'"
                alt="Profile Image"
                class="profile-image"
                style="width: 28px; height: 28px"
              />
            </span>
            <el-dropdown-menu slot="dropdown">
              <div class="user-info">
                <div
                  style="width: 100%; height: 70px; background-color: #0079bf"
                ></div>
                <div class="user-info-content">
                  <img
                    :src="'/user' + (idx + 1) + '.png'"
                    alt="Profile Image"
                    class="profile-image"
                    style="width: 50px; height: 50px; padding-top: 5px"
                  />
                  <div>
                    <span style="margin-left: 10px; font-size: 15px">
                      {{ rowData.userId }}
                    </span>
                    <span style="margin-left: 10px; color: #f35250f4">{{
                      rowData.userRole
                    }}</span>
                  </div>
                </div>
                <div
                  class="setAdmin"
                  v-if="rowData.userRole !== 'ADMIN' && isAdmin"
                  @click="updateAdmin(rowData.userId)"
                >
                  Add to WorkSpace as admin
                </div>
              </div>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
        <span v-if="adminList.length > 4" class="extra-admins">
          +{{ adminList.length - 4 }}
        </span>
      </div>
      <div class="board-user-share" @click="showDetailModal" v-if="isMember">
        <i class="fas fa-user-plus"></i>
        <span style="font-size: 14px">Share</span>
      </div>
      <div class="board-etc">
        <i class="fas fa-ellipsis-h"></i>
      </div>
    </div>
    <invite-member-modal
      :show="inviteDetailModalShow"
      @close="closeDetailModal"
    />
  </div>
</template>
<script>
import InviteMemberModal from "~/components/modal/InviteMemberModal";
export default {
  components: { InviteMemberModal },
  props: ["brdData"],
  data() {
    return {
      starStatus: 0,
      input1: "",
      brdCode: "brd1",
      adminList: [],
      visibility: "",
      inviteDetailModalShow: false,
      userId: "",
      isAdmin: "",
      isMember: "",
    };
  },
  computed: {
    visibilityText() {
      switch (this.visibility) {
        case "10":
          return "Public";
        case "00":
          return "Private";
        case "01":
          return "Workspace visible";
        default:
          return "Workspace visible";
      }
    },
    visibilityIcon() {
      switch (this.visibility) {
        case "10":
          return "fas fa-globe-asia";
        case "00":
          return "fas fa-user-lock";
        case "01":
          return "fas fa-user-friends";
        default:
          return "fas fa-user-friends";
      }
    },
  },
  methods: {
    activateFavorite() {
      if (this.starStatus == 0) {
        this.starStatus = 1;
        console.log(this.starStatus);
      } else {
        this.starStatus = 0;
        console.log(this.starStatus);
      }
    },
    getAdminInfo() {
      let data = {
        brdCode: this.brdCode,
      };
      this.$axios
        .post("/board/getBoardAdmin", data)
        .then((res) => {
          console.log(res.data);
          this.adminList = res.data;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // 회원 정보 불러오기
    async getMemberInfo() {
      // private이 아닌경우 return
      // if (this.visibility !== "00") {
      //   return;
      // }
      console.log(this.visibility);
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
              // 보드 멤버리스트(adminList)에서 접속한 유저가 있는지 판단
              this.isMember = this.adminList.some(
                (admin) => admin.userId === res.data.userId
              );
              this.userId = res.data.userId;
              let foundId = this.adminList.find(
                (e) => e.userId === this.userId
              );
              this.isAdmin = foundId.userRole === "ADMIN";

              // private일때, 없으면 권한 없음
              if (this.visibility === "00") {
                if (!this.isMember) {
                  alert("보드에 접근할 권한이 없습니다.");
                  this.$router.push("/");
                }
              }
            })
            .catch((error) => {
              console.log(error);
            });
        }
      } catch (error) {
        console.log(error);
      }
    },
    getBoardInfo() {
      let data = {
        brdCode: this.brdCode,
      };
      this.$axios
        .post("/board/getInfoBoard", data)
        .then((res) => {
          this.visibility = res.data.visibility;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    async updateAdmin(userId) {
      let data = {
        userId: userId,
      };
      await this.$axios
        .patch("/board/updateAdmin", data)
        .then((res) => {
          console.log("변경 성공");
          this.$router.go();
        })
        .catch((error) => {
          console.log(error);
        });
    },
    async updateVisibility(visibility) {
      let data = {
        brdCode: this.brdCode,
        visibility: visibility,
      };
      await this.$axios
        .patch("/board/updateVisibility", data)
        .then((res) => {
          console.log("변경 성공");
          this.getBoardInfo();
        })
        .catch((error) => {
          console.log(error);
        });
    },
    async updateBrdInfo() {
      let data = {
        brdCode: this.brdCode,
        brdName: this.brdData.brdName,
      };
      await this.$axios
        .patch("/board/updateBrdInfo", data)
        .then((res) => {
          console.log("변경 성공");
        })
        .catch((error) => {
          console.log(error);
        });
    },

    showDetailModal() {
      this.inviteDetailModalShow = true;
    },
    closeDetailModal() {
      this.inviteDetailModalShow = false;
    },
  },
  created() {
    this.getAdminInfo();
    this.getBoardInfo();
  },
  // 값변경시에 실행
  watch: {
    adminList() {
      this.getMemberInfo();
    },
  },
};
</script>
<style scoped>
.board-header {
  display: flex;
  justify-content: space-between;
}
.board-header-menu {
  display: flex;
}
.board-title-header {
  width: 100px;
}
.board-header-star {
  width: 25px;
  height: 25px;
  padding: 5px;
  margin-left: 5px;
  border-radius: 10%;
  text-align: center;
  border: 1px solid #b0cbcb;
  /* background-color: #b0cbcb; */
}
.favorite {
  width: 32px;
  height: 32px;
}
.board-works-visible {
  margin-left: 10px;
  width: 150px;
  height: 32px;
  padding-top: 3px;
  border-radius: 5px;
  text-align: center;
  color: black;
  border: 1px solid #b0cbcb;
}
.board-works-visible:hover {
  cursor: pointer;
}
.board-works-non-visible {
  margin-left: 10px;
  width: 150px;
  height: 32px;
  padding-top: 3px;
  border-radius: 5px;
  text-align: center;
  border: 1px solid #b0cbcb;
  color: gray;
  background-color: #f2f2f2;
}
.board-works-non-visible:hover {
  cursor: not-allowed;
}
.board-type {
  width: 80px;
  height: 32px;
  padding-top: 3px;
  border-radius: 5px;
  text-align: center;
  border: 1px solid #b0cbcb;
  margin-left: 10px;
}
.board-type-select {
  width: 32px;
  height: 32px;
  text-align: center;
  padding-top: 3px;
  border-radius: 5px;
  border: 1px solid #b0cbcb;
  margin-left: 5px;
}
.board-filter {
  width: 80px;
  height: 32px;
  text-align: center;
  padding-top: 3px;
  border-radius: 5px;
  border: 1px solid #b0cbcb;
  margin-right: 10px;
}
.board-filter:hover {
  cursor: pointer;
}
.board-user-profile {
  display: flex;
  min-width: 30px;
  height: 32px;
  padding-top: 3px;
  border-radius: 5px;
  text-align: center;
  margin-right: 10px;
}
.board-user-profile:hover {
  cursor: pointer;
}
.board-user-share {
  width: 80px;
  height: 32px;
  padding-top: 3px;
  border-radius: 5px;
  text-align: center;
  border: 1px solid #b0cbcb;
}
.board-user-share:hover {
  cursor: pointer;
}
.board-etc {
  width: 32px;
  height: 32px;
  text-align: center;
  padding-top: 3px;
  border-radius: 5px;
  border: 1px solid #b0cbcb;
  margin-left: 5px;
}
.works-dropdown {
  width: 300px;
  height: 220px;
}
.works-dropdown-title {
  height: 30px;
  border-bottom: 1px solid rgb(216, 216, 216);
  line-height: 30px;
  text-align: center;
  font-size: 13px;
}
.visibility-list {
  height: 100%;
  list-style: none;
  padding-left: 5px;
}
li.visibility {
  height: 25%;
  display: flex;
  flex-direction: column;
  font-size: 14px;
  padding: 5px;
}
li.visibility:hover {
  cursor: pointer;
  background-color: #f2f2f2;
}
.profile-image {
  margin-right: 5px;
}
.user-info {
  width: 300px;
  height: 220px;
}
.user-info-content {
  display: flex;
  height: 60px;
  line-height: 60px;
  padding: 10px;
  margin-left: 10px;
  border-bottom: 1px solid rgb(218, 218, 218);
}
.setAdmin {
  /* margin-left: 30px; */
  height: 50px;
  line-height: 50px;
  margin-top: 10px;
  padding-left: 20px;
  font-size: 14px;
}
.setAdmin:hover {
  background-color: #f2f2f2;
  cursor: pointer;
}
</style>
