<template>
  <div class="login-wrap">
    <div class="left-box">
      <div class="title-box">
        <div class="tit">城市打卡</div>
        <div class="tip">发现城市灵感，记录值得分享的日常片段。</div>
      </div>
    </div>
    <div class="login-form">
      <div class="title">用户登录</div>
      <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          :rules="rules"
          label-width="0px"
          class="demo-ruleForm"
          size="large"
      >
        <el-form-item prop="username">
          <el-input size="large" :prefix-icon="User" placeholder="账号" v-model="ruleForm.username"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" size="large" :prefix-icon="Lock" show-password
                    placeholder="密码" v-model="ruleForm.password"/>
        </el-form-item>

        <el-form-item>
          <el-button
              :loading="state.loading"
              style="width: 100%; margin-top: 10px"
              type="primary"
              @click="submitForm(ruleFormRef)">
            登 录
          </el-button>
        </el-form-item>
        <div class="login-link">
          <router-link to="/register">没有账号？去注册</router-link>
        </div>
      </el-form>
    </div>

  </div>
</template>

<script setup lang="ts">
  import { reactive, ref} from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessage, ElNotification } from 'element-plus';
  import { Lock, User } from '@element-plus/icons-vue';
  import { useUserStore } from '@/store/user';
  import { HOME_URL } from '@/config/config';
  import { login } from '@/apis';
  import { passwordRule, timeFix, usernameRule } from '@/utils';

  const router: any = useRouter();

  const userStore: any = useUserStore();

  // 表单ref
  const ruleFormRef = ref();

  // 变量
  const state = reactive({
    // 登录按钮的loading
    loading: false,
  });

  // 登录表单
  const ruleForm = reactive({
    username: 'admin',
    password: 'admin123'
  });

  const rules = reactive({
    username: { required: true, validator: usernameRule, trigger: 'blur' },
    password: { required: true, validator: passwordRule, trigger: 'blur' },
  });

  /**
   * 去登录
   */
  const submitForm = async (formEl: any) => {
    if (!formEl) return;
    await formEl.validate(async (valid: any) => {
      if (valid) {
        // verifyRef.value.show();
        try {
          state.loading = true;
          // 登录成功
          const res:any = await login({
            username: ruleForm.username,
            password: ruleForm.password,
          });
          userStore.setToken(res.data.token);
          router.replace(HOME_URL);
          ElNotification({ title: `hi,${timeFix()}!`, message: `欢迎回来`, type: 'success' });
        } catch (e) {
          ElMessage.error('登录异常，请重试！');
        } finally {
          state.loading = false;
        }
      }
    });
  };
</script>

<style scoped lang="scss">
  .login-wrap {
    display: flex;
    height: 100vh;
    overflow: hidden;
    background:
      radial-gradient(circle at 12% 0%, rgba(255, 92, 122, 0.18), transparent 32%),
      linear-gradient(180deg, #fff6f7 0%, #fff 220px),
      linear-gradient(180deg, #fff 0%, #fff8f6 100%);
  }

  .left-box {
    flex: 1;
    position: relative;

    .title-box {
      margin: 220px 120px;
      color: #1f2937;

      .tit {
        font-size: 40px;
        font-weight: 700;
      }

      .tip {
        margin: 20px 0;
        max-width: 420px;
        font-size: 15px;
        line-height: 1.8;
        color: #6b7280;
      }
    }
  }

  .login-form {
    width: 500px;
    height: 100%;
    padding: 80px 60px;
    overflow: hidden;
    box-sizing: border-box;
    border-left: 1px solid rgba(255, 92, 122, 0.1);
    backdrop-filter: blur(24px);
    background: rgba(255, 255, 255, 0.78);

    .title {
      margin: 15px 0 20px;
      color: #1f2937;
      font-size: 24px;
      font-weight: 700;
    }

    ::v-deep(.el-input__inner) {
      --el-input-inner-height: 50px
    }

    ::v-deep(.el-input__wrapper) {
      border-radius: 12px;
      box-shadow: 0 0 0 1px #f3dbe2 inset;
      background: rgba(255, 250, 251, 0.92);
    }

    ::v-deep(.el-input__wrapper.is-focus) {
      box-shadow: 0 0 0 1px #ff8aa0 inset;
    }

    ::v-deep(.el-button--primary) {
      border: 0;
      border-radius: 999px;
      background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
      box-shadow: 0 14px 30px rgba(255, 92, 122, 0.2);
    }
  }

  .login-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;

    a {
      color: #ff5c7a;
      text-decoration: none;
    }
  }

  @media (max-width: 960px) {
    .left-box {
      display: none;
    }

    .login-form {
      width: min(100%, 520px);
      margin: 0 auto;
      border-left: 0;
    }
  }

</style>
