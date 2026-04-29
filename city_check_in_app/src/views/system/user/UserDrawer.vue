<template>
  <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="450px"
      :title="`${drawerProps.title}用户`"
  >
    <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="drawerProps.rowData">

      <el-form-item label="用户名" prop="username">
        <el-input v-model="drawerProps.rowData.username" placeholder="请填写用户名" clearable/>
      </el-form-item>

      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="drawerProps.rowData.realName" placeholder="请填写真实姓名" clearable/>
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="drawerProps.rowData.email" placeholder="请填写邮箱" clearable/>
      </el-form-item>

      <el-form-item label="电话" prop="phone">
        <el-input v-model="drawerProps.rowData.phone" placeholder="请填写联系电话" clearable/>
      </el-form-item>

      <el-form-item label="角色列表" prop="rid" v-if="drawerProps.rowData?.isSuper !== 1">
        <el-radio-group v-model="drawerProps.rowData.rid">
          <el-radio
              v-for="item in state.allRolesList"
              :key="item.id"
              :label="item.id">
            {{ item.roleName }}
          </el-radio>
        </el-radio-group>

      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </template>

  </el-drawer>
</template>

<script setup lang="ts" name="UserDrawer">
  import { ref, reactive, watch } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { clearEmptyValue, emailRule, phoneRule, textRule, usernameRule } from '@/utils';
  import { useAuthStore } from '@/store/auth';

  const { authInfo }: any = useAuthStore();

  interface DrawerProps {
    title: string
    rowData?: any
    list?: any
    api?: (params: any) => Promise<any>
    getTableList?: () => Promise<any>
  }

  interface RolesState {
    allRolesList: any[]
  }

  const rules = reactive({
    username: { required: true, validator: usernameRule, trigger: 'blur' },
    email: { required: true, validator: emailRule, trigger: 'blur' },
    phone: { required: true, validator: phoneRule, trigger: 'blur' },
    rid: { required: true, message: '角色不能为空', trigger: 'blur' },
    realName: {
      required: true,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 10),
      trigger: 'blur'
    },
    airlineCode: { required: true, message: '航司代码不能为空', trigger: 'blur' },
    company:{
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 50),
      trigger: 'blur'
    }
  });

  const ruleFormRef = ref<FormInstance>();
  // drawer框状态
  const drawerVisible = ref(false);
  const drawerProps = ref<DrawerProps>({ title: '' });
  // loading
  const loading = ref<boolean>(false);

  // 角色选择状态管理
  const state: RolesState = reactive({
    // 所有角色
    allRolesList: [],
    // 分配的角色
    assignRole: -1,
    // checkAll: false,
    isIndeterminate: false,
  });

  // 接收父组件传过来的参数
  const acceptParams = (params: DrawerProps): void => {
    const { list } = params;
    state.allRolesList = list;
    drawerProps.value = params;
    drawerVisible.value = true;
  };

  const handleSubmit = () => {
    ruleFormRef.value!.validate(async (valid) => {
      if (!valid) return;
      try {
        loading.value = true;
        // 处理接口参数
        const { email, phone, username, rid, company, airlineCode, realName } = drawerProps.value.rowData;
        let params: any = { email, phone, username, sysRole: { id: rid }, company, airlineCode, realName };
        if (drawerProps.value.title === '编辑') {
          params.id = drawerProps.value.rowData.id;
        }
        await drawerProps.value.api!(clearEmptyValue(params));
        ElMessage.success({ message: `${drawerProps.value.title}用户成功！` });
        drawerProps.value.getTableList!();
        drawerVisible.value = false;
        loading.value = false;
      } catch (error) {
        loading.value = false;
      }
    });
  };

  defineExpose({
    acceptParams,
  });
</script>
<style lang="scss" scoped></style>
