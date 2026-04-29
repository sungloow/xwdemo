<template>
  <el-dialog
      v-model="dialogVisible"
      title="修改密码"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      width="500px">
    <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-width="120px"
        class="demo-ruleForm">
      <el-form-item label="旧密码" prop="password">
        <el-input v-model="ruleForm.password" type="password" show-password/>
      </el-form-item>

      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="ruleForm.newPassword" type="password" show-password/>
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="ruleForm.confirmPassword" type="password" show-password/>
      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm(ruleFormRef)" v-loading="loading">确认修改</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>

  import { reactive, ref } from 'vue';
  import { forgotPassword } from '../../apis';
  import { ElMessage } from 'element-plus';
  import { LOGIN_URL } from '@/config/config';
  import { useRoute, useRouter } from 'vue-router';
  import { useUserStore } from '@/store/user';

  const router = useRouter();
  const route = useRoute();
  const userStore: any = useUserStore();

  const ruleFormRef = ref<any>();
  // dialog框状态
  const dialogVisible = ref(false);
  // dialog loading
  const loading = ref(false);


  // 接收父组件点击事件
  const show = () => {
    dialogVisible.value = true;
  };

  const ruleForm = reactive({
    password: '',
    newPassword: '',
    confirmPassword: '',
  });

  const validateOldPass = (rule: any, value: any, callback: any) => {
    if (value === '') {
      callback(new Error('请输入原密码'));
    }
    callback();
  };

  const validatePass = (rule: any, value: any, callback: any) => {
    if (rule.required && !value) {
      return callback(new Error('新密码不能为空'));
    } else {
      const pattern = /^(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[\x21-\x2f\x3a-\x40\x5b-\x60\x7B-\x7F])[\da-zA-Z\x21-\x2f\x3a-\x40\x5b-\x60\x7B-\x7F]{8,}$/;
      if (!(pattern.test(value))) {
        callback(new Error('密码强度8位以上，且包含大小写字母、数字、特殊字符'));
      }
      if (ruleForm.confirmPassword !== '') {
        if (!ruleFormRef.value) return;
        ruleFormRef.value.validateField('confirmPassword', () => null);
      }
      callback();
    }
    callback();
  };

  const validatePass2 = (rule: any, value: any, callback: any) => {
    if (value === '') {
      callback(new Error('请确认新密码'));
    } else if (value !== ruleForm.newPassword) {
      callback(new Error('两次密码不一致'));
    } else {
      callback();
    }
  };

  const rules = reactive({
    password: [{ required: true, validator: validateOldPass, trigger: 'blur' }],
    newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
    confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
  });

  /**
   * 密码修改
   * @param formEl
   */
  const submitForm = (formEl: any | undefined) => {
    if (!formEl) return;
    formEl.validate(async (valid: any) => {
      if (valid) {
        try {
          loading.value = true;
          await forgotPassword(ruleForm);
          dialogVisible.value = false;
          // 修改成功后跳转登录页面重新登录
          await userStore.Logout();
          ElMessage.success({ message: '修改成功，即将跳转到登录页面...' });
          setTimeout(() => router.push({ path: LOGIN_URL, query: { redirect: route.fullPath } }), 3000);
        } finally {
          loading.value = false;
        }
      } else {
        return false;
      }
    });
  };

  defineExpose({
    show,
  });

</script>
