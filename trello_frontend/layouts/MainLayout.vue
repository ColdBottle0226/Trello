<template>
  <div class="common-layout" :style="{ backgroundColor: commonLayoutColor }">
    <el-container style="height: 100%">
      <el-header
        id="header"
        height="45px"
        :style="{ backgroundColor: headerColor }"
      >
        <Header @updateBackgroundColors="updateBackgroundColors" />
      </el-header>
      <el-container style="height: 100%">
        <el-aside id="aside" :width="asideWidth" style="height: 100%">
          <!-- collActive $emit -->
          <Aside @collActive="activeCollapse" :asideColor="this.asideColor" />
        </el-aside>
        <el-main style="height: 100%">
          <nuxt />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Header from "~/layouts/Header.vue";
import Aside from "~/layouts/Aside.vue";

export default {
  name: "MainLayout",
  components: { Header, Aside },
  data() {
    return {
      isCollapse: false,
      commonLayoutColor: "",
      headerColor: "",
      asideColor: "",
    };
  },
  methods: {
    activeCollapse(e) {
      this.isCollapse = e;
    },

    updateBackgroundColors({ commonLayoutColor, headerColor, asideColor }) {
      this.commonLayoutColor = commonLayoutColor;
      this.headerColor = headerColor;
      this.asideColor = asideColor;
    },
  },
  computed: {
    // collapse에 따른 넓이(true:70px,false:260px)
    asideWidth() {
      return this.isCollapse ? "70px" : "260px";
    },
  },
  watch: {},
};
</script>

<style scoped>
.common-layout {
  height: 100vh;
  /* background-color: #fda7a7; */
}
#header {
  background-color: #026aa7;
  /* */
  /* background-color: #f66868; */

  color: white;
}
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
