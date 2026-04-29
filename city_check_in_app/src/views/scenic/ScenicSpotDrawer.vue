<template>
  <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="450px"
      :title="`${drawerProps.title}景点`"
  >
    <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="formData">

      <el-form-item label="景点名称" prop="name">
        <el-input v-model="formData.name" placeholder="请填写景点名称" clearable/>
      </el-form-item>

      <el-form-item label="所属区县" prop="districtId">
        <el-select v-model="formData.districtId" placeholder="请选择所属区县" clearable>
          <el-option
              v-for="district in districts"
              :key="district.id"
              :label="district.name"
              :value="district.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="景点描述" prop="description">
        <el-input v-model="formData.description" placeholder="请填写景点描述" clearable type="textarea" :rows="3"/>
      </el-form-item>

      <el-form-item label="景点地址" prop="address">
        <el-input v-model="formData.address" placeholder="请填写景点地址" clearable/>
      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </template>

  </el-drawer>
</template>

<script setup lang="ts" name="ScenicSpotDrawer">
  import { ref, reactive, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { clearEmptyValue, textRule } from '@/utils';
  import { getDistrictList } from '@/apis';

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
    districtId: { required: true, message: '请选择所属区县', trigger: 'blur' },
    description: {
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 200),
      trigger: 'blur'
    },
    address: {
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 100),
      trigger: 'blur'
    }
  });

  const ruleFormRef = ref<FormInstance>();
  // drawer框状态
  const drawerVisible = ref(false);
  const drawerProps = ref<DrawerProps>({ title: '' });
  // 表单数据
  const formData = ref({
    id: '',
    name: '',
    districtId: '',
    description: '',
    address: ''
  });
  // 区县列表
  const districts = ref<any[]>([]);
  // loading
  const loading = ref<boolean>(false);

  // 接收父组件传过来的参数
  const acceptParams = (params: DrawerProps): void => {
    drawerProps.value = params;
    // 填充表单数据
    formData.value = {
      id: params.rowData.id || '',
      name: params.rowData.name || '',
      districtId: params.rowData.districtId || '',
      description: params.rowData.description || '',
      address: params.rowData.address || ''
    };
    drawerVisible.value = true;
  };

  // 初始化区县数据
  const initDistrictData = async () => {
    try {
      const districtRes = await getDistrictList();
      districts.value = districtRes.data;
    } catch (error) {
      console.error('获取区县数据失败:', error);
    }
  };

  const handleSubmit = () => {
    ruleFormRef.value!.validate(async (valid) => {
      if (!valid) return;
      try {
        loading.value = true;
        // 处理接口参数
        const params = clearEmptyValue(formData.value);
        await drawerProps.value.api!(params);
        ElMessage.success({ message: `${drawerProps.value.title}景点成功！` });
        drawerProps.value.getTableList!();
        drawerVisible.value = false;
        loading.value = false;
      } catch (error) {
        loading.value = false;
      }
    });
  };

  // 组件挂载时初始化区县数据
  onMounted(() => {
    initDistrictData();
  });

  defineExpose({
    acceptParams,
  });
</script>
<style lang="scss" scoped></style>