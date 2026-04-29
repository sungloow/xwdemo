<template>
  <div>
    <el-dropdown @visible-change="onChange" @command="handleCommand">
      <div class="avatar-dropdown">
        <!--        <img class="user-avatar" :src="state.avatar" alt=""/>-->
        <div class="user-avatar">{{ state.username.slice(0,1) }}</div>
        <div class="user-name">{{ state.username }}</div>
        <el-icon class="up-down">
          <component :is="visible ? 'ArrowUp' : 'ArrowDown'"/>
        </el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="changePassword">
            <el-icon size="16px">
              <Edit/>
            </el-icon>
            <span>修改密码</span>
          </el-dropdown-item>

          <el-dropdown-item command="logout">
            <el-icon size="16px">
              <CircleClose/>
            </el-icon>
            <span>退出登陆</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 修改密码弹窗 -->
    <UpdatePassword ref="updatePasswordRef"/>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import { useUserStore } from '@/store/user';
  import { ArrowUp, ArrowDown } from '@element-plus/icons-vue';
  import { LOGIN_URL } from '@/config/config';

  // dropdown展示
  const visible = ref(false);
  // 更改密码弹窗
  const updatePasswordRef = ref<any>(null);

  const router = useRouter();
  // const route = useRoute();
  const userStore: any = useUserStore();

  const state: any = reactive({
    username: userStore.userInfo.username,
  });

  const onChange = (show: boolean) => {
    visible.value = show;
  };

  const handleCommand = (command: string) => {
    if (command === 'logout') {
      logout();
    }
    if (command === 'changePassword') {
      updatePasswordRef.value.show();
    }
  };

  /**
   * 退出登陆
   */
  const logout = () => {
    ElMessageBox.confirm('您确定要退出吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      await userStore.Logout();
      // router.push({ path: LOGIN_URL, query: { redirect: route.fullPath } });
      router.push({ path: LOGIN_URL });
      ElMessage.success('退出登录成功！');
    });
  };

</script>

<style scoped lang="scss">
  .badge-item {
    margin-top: 15px;
    margin-right: 40px;
  }

  .message-icon:hover {
    color: #333;
    cursor: pointer;
  }

  .avatar-dropdown {
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: center;
    justify-items: center;
    height: 50px;
    padding: 0;

    .user-avatar {
      width: 40px;
      height: 40px;
      cursor: pointer;
      border-radius: 50%;
      background-image: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
      text-align: center;
      line-height: 40px;
      color: #fff;
      font-size: 24px;
    }

    .user-name {
      position: relative;
      margin: 0 6px;
      cursor: pointer;
    }
  }
</style>
