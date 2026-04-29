<template>
  <div class="login-wrap">
    <div class="left-box">
      <div class="title-box">
        <div class="tit">城市打卡</div>
        <div class="tip">发现城市灵感，记录值得分享的日常片段。</div>
      </div>
    </div>
    <div class="login-form">
      <div class="title">用户注册</div>
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
        <el-form-item prop="realName">
          <el-input size="large" :prefix-icon="Avatar" placeholder="真实姓名" v-model="ruleForm.realName"/>
        </el-form-item>
        <el-form-item prop="email">
          <el-input size="large" :prefix-icon="Message" placeholder="邮箱" v-model="ruleForm.email"/>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input size="large" :prefix-icon="Phone" placeholder="手机号" v-model="ruleForm.phone"/>
        </el-form-item>

        <el-form-item>
          <el-button
              :loading="state.loading"
              style="width: 100%; margin-top: 10px"
              type="primary"
              @click="submitForm(ruleFormRef)">
            注 册
          </el-button>
        </el-form-item>
        <div class="login-link">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </div>

  </div>
</template>

<script setup lang="ts">
  import { reactive, ref} from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { ElMessage, ElNotification } from 'element-plus';
  import { Lock, User, Avatar, Message, Phone } from '@element-plus/icons-vue';
  import { useUserStore } from '@/store/user';
  import { LOGIN_URL } from '@/config/config';
  import { register } from '@/apis';
  import { passwordRule, timeFix, usernameRule } from '@/utils';

  const router: any = useRouter();
  const route: any = useRoute();

  const userStore: any = useUserStore();

  // 表单ref
  const ruleFormRef = ref();

  // 变量
  const state = reactive({
    // 注册按钮的loading
    loading: false,
  });

  // 注册表单
  const ruleForm = reactive({
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: ''
  });

  const rules = reactive({
    username: { required: true, validator: usernameRule, trigger: 'blur' },
    password: { required: true, validator: passwordRule, trigger: 'blur' },
    realName: { required: true, message: '请输入真实姓名', trigger: 'blur' },
    email: { 
      required: true, 
      type: 'email', 
      message: '请输入正确的邮箱地址', 
      trigger: 'blur' 
    },
    phone: { 
      required: true, 
      pattern: /^1[3-9]\d{9}$/, 
      message: '请输入正确的手机号', 
      trigger: 'blur' 
    }
  });

  /**
   * 去注册
   */
  const submitForm = async (formEl: any) => {
    if (!formEl) return;
    await formEl.validate(async (valid: any) => {
      if (valid) {
        try {
          state.loading = true;
          // 注册成功
          const res:any = await register({
            username: ruleForm.username,
            password: ruleForm.password,
            realName: ruleForm.realName,
            email: ruleForm.email,
            phone: ruleForm.phone,
          });
          ElNotification({ title: '注册成功', message: '请登录', type: 'success' });
          router.replace(LOGIN_URL);
        } catch (e) {
          ElMessage.error('注册异常，请重试！');
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
        color: #6b7280;
        font-size: 15px;
        line-height: 1.8;
      }
    }

  }

  .login-form {
    width: 500px;
    height: 100%;
    padding: 64px 60px;
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
