<template>
  <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="450px"
      :title="`${drawerProps.title}美食种类`"
  >
    <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="drawerProps.rowData">

      <el-form-item label="名称" prop="name">
        <el-input v-model="drawerProps.rowData.name" placeholder="请填写美食种类名称" clearable/>
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input v-model="drawerProps.rowData.description" placeholder="请填写美食种类描述" clearable type="textarea" :rows="3"/>
      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </template>

  </el-drawer>
</template>

<script setup lang="ts" name="FoodTypeDrawer">
  import { ref, reactive } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { clearEmptyValue, textRule } from '@/utils';

  interface DrawerProps {
    title: string
    rowData?: any
    api?: (params: any) => Promise<any>
    getTableList?: () => Promise<any>
  }

  const rules = reactive({
    name: {
      required: true,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 50),
      trigger: 'blur'
    },
    description: {
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 200),
      trigger: 'blur'
    }
  });

  const ruleFormRef = ref<FormInstance>();
  // drawer框状态
  const drawerVisible = ref(false);
  const drawerProps = ref<DrawerProps>({ title: '' });
  // loading
  const loading = ref<boolean>(false);

  // 接收父组件传过来的参数
  const acceptParams = (params: DrawerProps): void => {
    drawerProps.value = params;
    drawerVisible.value = true;
  };

  const handleSubmit = () => {
    ruleFormRef.value!.validate(async (valid) => {
      if (!valid) return;
      try {
        loading.value = true;
        // 处理接口参数
        const { name, description } = drawerProps.value.rowData;
        let params: any = { name, description };
        if (drawerProps.value.title === '编辑') {
          params.id = drawerProps.value.rowData.id;
        }
        await drawerProps.value.api!(clearEmptyValue(params));
        ElMessage.success({ message: `${drawerProps.value.title}美食种类成功！` });
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
