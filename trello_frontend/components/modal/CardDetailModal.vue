<template lang="">
  <div>
    <el-dialog
      width="800px"
      :visible.sync="show"
      @close="$emit('close')"
      style="color: gray"
    >
      <template v-slot:title>
        <div class="custom-header">
          <h3 style="color: black;display:flex">
            <span style="margin-right: 20px"
              ><i class="fas fa-credit-card"></i></span
            >
            {{ form.title }}
          </h3>
          <div class="card-detail-header">
            in list
            <span style="text-decoration: underline">{{ form.listName }}</span>
          </div>
        </div>
      </template>

      <div class="card-detail-modal">
        <div class="card-detail-content">
          <div class="content-func">
            <div class="noti">
              <div class="noti-title">Notifications</div>
              <div class="custom-btn">
                <i class="fas fa-eye"></i>
                <span> Watch </span>
              </div>
            </div>

            <!-- Description 영역 -->
            <div class="desc">
              <div class="desc-header">
                <span class="desc-icon">
                  <i class="fas fa-align-justify"></i>
                </span>
                <div class="description-title">
                  <span> Description </span>
                </div>
                <div
                  class="custom-btn"
                  v-if="isFormContentValid"
                  @click="editorShow"
                >
                  Edit
                </div>
              </div>
              <!-- Editor 활성화 이전 && content 없을 시에-->
              <div
                v-if="isEditorShow === false && !isFormContentValid"
                class="before-editor"
                @click="editorShow"
              >
                Add a more detailed description...
              </div>
              <!-- Editor 활성화 이전 && content 있을 시에-->
              <div
                v-if="isEditorShow === false && isFormContentValid"
                class="content-available"
              >
                {{ form.content }}
              </div>
              <!-- Add a Description 클릭 이후(editor 활성화) -->
              <div class="editor-area" v-show="isEditorShow">
                <editor
                  :initialValue="text"
                  :initialEditType="editType"
                  :options="options"
                  ref="editor"
                  align="left"
                  style="width: 500px"
                ></editor>
                <viewer v-if="toggle" :initaialValue="text"></viewer>
                <div style="margin-top: 10px">
                  <el-button
                    type="primary"
                    :style="{ backgroundColor: '#0079bf' }"
                    @click="saveEvent"
                    >save</el-button
                  >
                  <el-button type="info" @click="editorClose">cancel</el-button>
                </div>
              </div>
            </div>
            <!-- Attachment 존재 시 -->
            <div class="attach" v-show="this.cardAttach.length !== 0">
              <div class="attach-group">
                <span class="attach-icon">
                  <i class="fas fa-paperclip"></i>
                </span>
                <div class="attach-title">Attachments</div>
              </div>
              <div
                class="attach-content"
                v-for="(rowData, idx) in cardAttach"
                :key="idx"
              >
                <template
                  v-if="
                    rowData.fileLoc.endsWith('.jpg') ||
                    rowData.fileLoc.endsWith('.png') ||
                    rowData.fileLoc.endsWith('.jpeg')
                  "
                >
                  <img
                    :src="'http://localhost:8080' + rowData.fileLoc"
                    class="attach-img"
                  />
                </template>
                <template v-else>
                  <span class="file-extension">{{
                    getFileExtension(rowData.fileLoc)
                  }}</span>
                </template>

                <div clsas="attach-file-content">
                  <span class="org-file-name">
                    {{ getOriginalImgName(rowData.fileLoc) }}
                  </span>
                  <span
                    class="attach-file-download"
                    @click="downloadFile(rowData.fileLoc)"
                    >Downloads</span
                  >
                  <span
                    class="attach-file-delete"
                    @click="removeAttach(rowData.attachCode)"
                    >Delete</span
                  >
                </div>
              </div>
            </div>

            <!-- Activity 영역 -->
            <div class="activ">
              <div class="activ-header">
                <div class="activ-group">
                  <span class="activ-icon">
                    <i class="fas fa-list" style="color: #5c5c5c"></i>
                  </span>
                  <div class="activ-title">Activity</div>
                </div>
                <div class="custom-btn">Show Detail</div>
              </div>
              <div class="activ-content">
                <div>
                  <img
                    src="/user.png"
                    alt="Profile Image"
                    class="profile-image"
                  />
                </div>
                <div class="comment-box">
                  <input
                    name=""
                    id=""
                    cols="30"
                    rows="10"
                    class="comment-textarea"
                    placeholder="Write a comment..."
                    v-model="activeForm.content"
                    @input="handleInput"
                  ></textarea>
                  <div style="display: flex">
                    <el-button
                      type="primary"
                      :style="{
                        backgroundColor: '#0079bf',
                        height: '30px',
                        width: '50px',
                        textAlign: 'center',
                        fontSize: '13px',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                      }"
                      @click="saveActivity(rowData)"
                      >Save</el-button
                    >
                    <span class="mention-icon" @click="showGrpUser">
                      <i class="far fa-at fa-xs"></i>
                    </span>
                  </div>
                </div>
              </div>
              <div
                class="card-active"
                v-for="(rowData, idx) in cardActive"
                :key="idx"
              >
                <div>
                  <img
                    src="/user.png"
                    alt="Profile Image"
                    class="profile-image"
                  />
                </div>
                <div class="card-active-box-area">
                  <div>
                    <span style="font-weight: 800">{{ rowData.creId }}</span>
                    <span style="margin-left: 10px; font-size: 13px">
                      {{ getTimeDiff(rowData.creDt) }}</span
                    >
                    <span style="font-size: 13px" v-if="rowData.udtYn === 'Y'"
                      >(edited)</span
                    >
                  </div>
                  <div
                    class="card-active-box"
                    v-if="rowData !== editedActivity"
                  >
                  
                    <span class="mention" v-if="rowData.cmntId">@{{ rowData.cmntId }}</span>
                    <span>{{ rowData.content }}</span>
                  </div>
                  <!-- activity Edit 눌렀을 경우 -->
                  <div class="activity-edit-textarea" v-else>
                    <textarea
                      name=""
                      id=""
                      cols="30"
                      rows="10"
                      class="comment-textarea"
                      v-model="rowData.content"
                      placeholder="Write a comment..."
                    ></textarea>
                    <div style="display: flex">
                      <el-button
                        type="primary"
                        :style="{
                          backgroundColor: '#0079bf',
                          height: '30px',
                          width: '50px',
                          textAlign: 'center',
                          fontSize: '13px',
                          display: 'flex',
                          justifyContent: 'center',
                          alignItems: 'center',
                        }"
                        v-model="rowData.content"
                        @click="updateActivity(rowData)"
                        >Save</el-button
                      >
                      <el-button
                        type="primary"
                        :style="{
                          backgroundColor: '#e3e3e3',
                          border: '1px solid #e3e3e3',
                          color: 'gray',
                          height: '30px',
                          width: '50px',
                          textAlign: 'center',
                          fontSize: '13px',
                          display: 'flex',
                          justifyContent: 'center',
                          alignItems: 'center',
                        }"
                        @click="editedActivity = null"
                        >Cancel</el-button
                      >
                    </div>
                  </div>

                  <div class="card-active-edit-area">
                    <i
                      class="far fa-heart"
                      style="color: #f03333; font-size: 15px"
                    ></i>
                    <div class="card-active-edit" @click="editActive(rowData)">
                      Edit
                    </div>
                    <div
                      class="card-active-delete"
                      @click="deleteActivity(rowData.acCode)"
                    >
                      Delete
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 사이드 영역 -->
          <div class="content-aside">
            <div>
              <div>Add to card</div>
              <div class="add-selection">
                <span>
                  <i class="far fa-user"></i>
                </span>
                <span> Members </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="fas fa-tag"></i>
                </span>
                <span> Labels </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="far fa-check-square"></i>
                </span>
                <span> Checklist </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="far fa-clock"></i>
                </span>
                <span> Dates </span>
              </div>
              <div class="add-selection" id="attach-selection">
                <span>
                  <i class="fas fa-paperclip"></i>
                </span>
                <span @click="showFileUpload" class="attach-span-area">
                  Attachment
                </span>
                <input
                  ref="fileInput"
                  type="file"
                  @change="handleFileSelect"
                  style="display: none"
                />
                <div v-if="fileUploadVisible" class="file-upload-modal">
                  <input type="file" @change="handleFileSelect" />
                  <button @click="hideFileUpload">Cancel</button>
                </div>
              </div>
            </div>
            <div class="action-area">
              <div>Actions</div>
              <div class="add-selection">
                <span>
                  <i class="fas fa-arrow-right"></i>
                </span>
                <span> Move </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="far fa-copy"></i>
                </span>
                <span> Copy </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="far fa-window-restore"></i>
                </span>
                <span> Make template </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="fas fa-archive"></i>
                </span>
                <span> Archive </span>
              </div>
              <div class="add-selection">
                <span>
                  <i class="fas fa-share-alt"></i>
                </span>
                <span> Share </span>
              </div>
            </div>
          </div>
        </div>
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
      form: {
        title: "",
        listName: "",
        content: "",
        imgCover: "",
        listCode: "",
      },
      text: "",
      // 에디터 타입 (what you see is what you get)
      editType: "wysiwyg",
      toggle: false,
      // hook 옵션(이미지 Blob처리)
      options: {
        language: "ko",
        hooks: {
          addImageBlobHook: this.addImageBlobHook,
        },
      },
      // editor 활성화
      isEditorShow: false,
      // 카드 첨부파일
      cardAttach: [],
      //activity 폼
      activeForm: {
        content: "",
        cmntId: "",
        cmntAtt: "",
        likeCnt: 0,
      },
      // 카드 activity
      cardActive: [],
      editedActivity: null,
      fileUploadVisible: false,
      // 로그인 id
      loginId: "",
    };
  },
  computed: {
    // content 존재 computed
    isFormContentValid() {
      return this.form.content !== "" && this.form.content !== null;
    },
  },
  props: ["show", "cardCode", "listCode"],
  watch: {
    show() {
      if (this.show == true) {
        this.getCardInfo();
        this.getAttachInfo();
        this.getActivityInfo();
        this.getLoginId();
      } else {
        this.init();
        this.$emit("close");
      }
    },
    cardActive() {
      for (let i in this.cardActive) {
        if (this.cardActive[i].content.startsWith('@')) {
          const spaceIndex = this.cardActive[i].content.indexOf(' ');
          if (spaceIndex > 0) {
            const modifiedContent = this.cardActive[i].content.slice(spaceIndex + 1);
            this.cardActive[i].content = modifiedContent;
          }
        }
        console.log(i, this.cardActive[i].content);
      }
    },
  },
  methods: {
    init() {
      this.form.title = "";
      this.form.listName = "";
      this.form.content = "";
      this.activeForm.cmntId = "";
      this.activeForm.cmntAtt = "";
      this.activeForm.likeCnt = 0;
      this.activeForm.content = "";
      this.isEditorShow = false;
    },
    editorShow() {
      this.isEditorShow = true;
    },
    editorClose() {
      this.isEditorShow = false;
    },
    showFileUpload() {
      this.fileUploadVisible = true;
    },
    hideFileUpload() {
      this.fileUploadVisible = false;
    },
    async getLoginId() {
      await this.$axios
      .get("/member/getMemberInfo", {
              headers: {
                Authorization: "Bearer " + localStorage .getItem("accessToken"),
              },
            })
        .then((res) => {
          this.loginId = res.data.userId;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // 이미지 원래 파일명 반환
    getOriginalImgName(imgLoc) {
      if (!imgLoc || typeof imgLoc !== "string") {
        return "";
      }
      // _와 숫자 제거
      const orgFileName = imgLoc.split("/upload/")[1].replace(/_\d+\./, ".");
      return orgFileName;
    },
    getFileExtension(fileLoc) {
      return fileLoc.split(".").pop();
    },
    // 해당 activity edit
    editActive(rowData) {
      // rowData 삽입
      this.editedActivity = rowData;
    },

    // 카드 정보 가져오기
    async getCardInfo() {
      let data = {
        cardCode: this.cardCode,
      };
      await this.$axios
        .post("/card/getCardInfo", data)
        .then((res) => {
          this.form = res.data;
          // 내용이 있을시에
          if (this.form.content !== "" && this.form.content !== null) {
            // DB에서 반환받은 내용을 텍스트 에디터 내에 할당
            this.$refs.editor.invoke("setMarkdown", this.form.content, true);
          } else {
            this.$refs.editor.invoke("setMarkdown", "", true);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // 카드 첨부 파일 가져오기
    async getAttachInfo() {
      let data = {
        cardCode: this.cardCode,
      };
      await this.$axios
        .post("/attach/getAttachInfo", data)
        .then((res) => {
          console.log(res.data);
          this.cardAttach = res.data;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // 텍스트 에디터 저장
    saveEvent() {
      const content = this.$refs.editor.invoke("getMarkdown");
      this.form.content = content;
      console.log(content);
      console.log(this.form);

      this.updateCardInfo();
      // 에디터 닫기
      this.editorClose();
    },
    // 카드 내용 저장
    async updateCardInfo() {
      this.form.listCode = this.listCode;

      await this.$axios
        .patch("/card/updateCard", this.form)
        .then((res) => {
          console.log("내용 저장");
          // 업데이트 정보 불러오기
          this.getCardInfo();
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // hook을 이용한 이미지 blob 변환 처리
    async addImageBlobHook(blob) {
      console.log(blob);

      const data = new FormData();

      data.append("uploadfile", blob);
      data.append("formData", this.formData);

      await this.$axios
        .post("/upload/file", data, {
          //multipart 설정
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          this.form.imgCover = res.data;
          console.log(this.form.imgCover);

          // 첨부파일 경로에 저장
          this.insertToAttach(this.form);
          // 에디터에 등록한 사진 넣기
          // this.$refs.editor.invoke(
          //   "setHTML",
          //   `<img src="http://localhost:8080${this.form.imgCover}" contenteditable="false" />`
          // );
          // 이미지 업로드 팝업 닫기
          document
            .getElementsByClassName("toastui-editor-close-button")[0]
            .click();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    // 이미지 첨부 -> attach 삽입
    async insertToAttach(formData) {
      let data = {
        fileLoc: formData.imgCover,
        cardCode: formData.cardCode,
      };
      console.log(data);
      await this.$axios
        .post("/attach/insertAttach", data)
        .then((res) => {
          console.log("첨부파일 저장 성공");

          // 첨부한 내용 새로고침
          this.getAttachInfo();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    // 첨부파일 제거 이벤트
    async removeAttach(attachCode) {
      await this.$axios
        .delete("/attach/deleteAttach?attachCode=" + attachCode)
        .then((res) => {
          console.log("삭제 성공");
          this.getAttachInfo();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    // 카드 activity 가져오기
    async getActivityInfo() {
      let data = {
        cardCode: this.cardCode,
      };
      await this.$axios
        .post("/activity/getActivityInfo", data)
        .then((res) => {
          console.log(res.data);
          this.cardActive = res.data;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // activity 저장
    async saveActivity() {
      this.activeForm.cardCode = this.cardCode;
      this.activeForm.creId = this.loginId;
      await this.$axios
        .post("/activity/insertActivity", this.activeForm)
        .then((res) => {
          console.log("activity 저장");
          this.getActivityInfo();
          this.activeForm.content = "";
          this.activeForm.cmntId = "";
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // activity 수정
    async updateActivity(rowData) {
      console.log(rowData);
      await this.$axios
        .patch("/activity/updateActivityInfo", rowData)
        .then((res) => {
          console.log("activity 수정");
          this.getActivityInfo();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    // activity 삭제
    async deleteActivity(acCode) {
      let data = {
        acCode: acCode,
      };
      await this.$axios
        .patch("/activity/updateActivityDelYn", data)
        .then((res) => {
          console.log("activity 삭제");
          this.getActivityInfo();
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // 지난시간 비교
    getTimeDiff(dateStr) {
      if (dateStr !== null && dateStr !== "") {
        const date1 = new Date(
          // 연도(0~4번째)
          dateStr.substr(0, 4),
          // 월(4~6) Date()에서 월은 0~11 이기때문에 -1
          dateStr.substr(4, 2) - 1,
          // 일(6~8)
          dateStr.substr(6, 2),
          // 시간(8~10)
          dateStr.substr(8, 2),
          // 분(10~12)
          dateStr.substr(10, 2),
          // 초(12~14)
          dateStr.substr(12, 2)
        );
        // 현재 시간
        const date2 = new Date();
        const diffMs = date2.getTime() - date1.getTime();
        // 분 차이 구하기
        const diffMins = Math.round(diffMs / 60000);
        // 1시간 미만 지난경우
        if (diffMins < 60) {
          return diffMins + " minutes ago";
        } else {
          const diffHours = Math.floor(diffMins / 60);
          // 1시간 이상 지난 경우
          if (diffHours < 24) {
            return diffHours + " hour" + (diffHours > 1 ? "s" : "") + " ago";
          } else {
            const diffDays = Math.floor(diffHours / 24);
            return diffDays + " day" + (diffDays > 1 ? "s" : "") + " ago";
          }
        }
      }
    },

    // 파일첨부
    async handleFileSelect(e) {
      const file = e.target.files[0];
      const formData = new FormData();
      formData.append("uploadfile", file);
      await this.$axios
        .post("/upload/file", formData)
        .then((res) => {
          console.log("file 업로드");
          // return 파일 경로로
          this.form.imgCover = res.data;
          this.insertToAttach(this.form);
        })
        .catch((error) => {
          console.log(error);
        });
    },

    // 파일다운로드
    async downloadFile(fileLoc) {
      // /upload/ 제외 파일경로
      const orgFileLoc = fileLoc.split("/upload/")[1];
      // 다운로드할 이름 (날짜수식 제거 file_20230322.pdf => file.pdf)
      const orgFileName = orgFileLoc.replace(/_\d+\./, ".");
      // 다운받을 파일 호출 api
      const downloadUrl = "/upload/download?fileLoc=" + orgFileLoc;
      try {
        const response = await this.$axios({
          url: downloadUrl,
          method: "GET",
          responseType: "blob",
        });
        console.log(response.data);
        //  Blob 이미지 띄울 url 생성
        const url = window.URL.createObjectURL(new Blob([response.data]));
        console.log(url);
        // a태그 동적 사용
        const link = document.createElement("a");
        // a태그 href = url
        link.href = url;
        // orgFileName으로 다운로드
        link.setAttribute("download", orgFileName);
        document.body.appendChild(link);
        // 링크 클릭
        link.click();
      } catch (error) {
        console.log(error);
      }
    },
    showGrpUser() {
      console.log(1);
    },
    // 멘션 아이디만 추출
    handleInput() {
    const content = this.activeForm.content;
    if (content.startsWith('@')) {
      const spaceIndex = content.indexOf(' ');
      if (spaceIndex > 0) {
        this.activeForm.cmntId = content.slice(1, spaceIndex);
      }
    }
  },
  
  },
};
</script>
<style scoped>
.card-detail-content {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  min-height: 700px;
}
.content-aside {
  width: 200px;
}
.content-func {
  width: 540px;
}
.noti {
  height: 70px;
  margin-left: 40px;
}
.desc {
  min-height: 100px;
}
.desc-header {
  display: flex;
}
.desc-icon {
  width: 40px;
}
.attach {
  min-height: 100px;
  margin-top: 20px;
}
.attach-icon {
  width: 40px;
}
.attach-group {
  display: flex;
}
.attach-title {
  width: 120px;
  font-weight: 1000;
  font-size: 17px;
}
.attach-content {
  margin-left: 40px;
  display: flex;
}
.attach-file-download {
  text-decoration: underline;
  margin-left: 20px;
}
.attach-file-delete {
  text-decoration: underline;
  margin-left: 5px;
}
.attach-file-delete:hover,
.attach-file-download:hover {
  cursor: pointer;
}
.attach-img {
  width: 110px;
  height: 80px;
  margin-top: 10px;
}
.activ-header {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
}
.activ-icon {
  width: 40px;
}
.activ-group {
  display: flex;
}
.activ-title {
  width: 100px;
  font-weight: 1000;
  font-size: 17px;
}
.activ-content {
  margin-top: 5px;
  display: flex;
}
.add-selection {
  height: 40px;
  background-color: #f7f7f7db;
  margin-top: 5px;
  border-radius: 3px;
  line-height: 40px;
}
.custom-btn {
  background-color: #f7f7f7db;
  width: 80px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 3px;
}
.custom-btn:hover {
  cursor: pointer;
  background-color: #e5e5e5db;
  border-radius: 3px;
}
.action-area {
  margin-top: 20px;
}
.card-detail-header {
  margin-left: 40px;
  font-size: 13px;
  color: gray;
}
.noti-title {
  font-size: 13px;
  font-weight: 800;
  padding-bottom: 5px;
}
.description-title {
  width: 100px;
  font-weight: 1000;
  font-size: 17px;
  margin-bottom: 10px;
}
.editor-area {
  margin-left: 40px;
}
.before-editor {
  height: 60px;
  width: 450px;
  margin-left: 40px;
  border-radius: 5px;
  background-color: #efefef;
  color: gray;
  padding: 5px;
}
.before-editor:hover {
  cursor: pointer;
}
.content-available {
  margin-left: 40px;
}
.org-file-name {
  margin-left: 15px;
  line-height: 90px;
  font-weight: 700;
}
.comment-box {
  margin-left: 20px;
  border-radius: 3px;
  padding: 5px;
  border: 1px solid #e3e3e3;
  box-shadow: 0px 2px 1px 0px #c6c6c6;
}
.comment-textarea {
  border: none;
  background-color: transparent;
  height: 50px;
  width: 480px;
  font-size: 15px;
  font-weight: 500;
  font-family: Arial, sans-serif;
}
.card-active {
  display: flex;
  margin-top: 20px;
}
.card-active-box-area {
  margin-left: 20px;
}
.card-active-box {
  border-radius: 3px;
  padding: 5px;
  border: 1px solid #e3e3e3;
  box-shadow: 0px 2px 1px 0px #c6c6c6;
  background-color: transparent;
  min-height: 35px;
  line-height: 35px;
  width: 480px;
  font-size: 15px;
  font-weight: 500;
  font-family: Arial, sans-serif;
}
.card-active-edit,
.card-active-delete {
  margin-left: 7px;
  color: gray;
  text-decoration: underline;
}
.card-active-edit:hover,
.card-active-delete:hover {
  cursor: pointer;
}
.card-active-edit-area {
  display: flex;
  margin-top: 7px;
  font-size: 12px;
}
.activity-edit-textarea {
  border-radius: 3px;
  padding: 5px;
  border: 1px solid #e3e3e3;
  box-shadow: 0px 2px 1px 0px #c6c6c6;
}
.file-upload-modal {
  height: 80px;
  position: relative;
  width: 200px;
  z-index: 9999;
  background-color: #c6c6c6;
  border-radius: 2px;
  padding: 5px;
}
.attach-span-area:hover {
  cursor: pointer;
  font-size: 10px;
}
.mention-icon {
  margin-left: 380px;
}
.mention-icon:hover {
  cursor: pointer;
}
.file-extension {
  width: 110px;
  height: 80px;
  line-height: 70px;
  text-align: center;
  border-radius: 2px;
  background-color: #ececec;
  margin-top: 15px;
  font-size: 17px;
  font-weight: 1000;
}
.mention {
  color: white;
  background-color: #653aff;
  height:40px;
  line-height:40px;
  padding:5px;
  border-radius:9px;
}
</style>
