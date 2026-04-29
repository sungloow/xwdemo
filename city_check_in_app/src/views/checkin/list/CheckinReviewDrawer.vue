<template>
  <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="450px"
      title="审核打卡记录"
  >
    <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="formData">

      <el-form-item label="打卡标题">
        <el-input v-model="formData.title" disabled/>
      </el-form-item>

      <el-form-item label="打卡类型">
        <el-tag :type="formData.type === 'food' ? 'warning' : 'primary'">
          {{ formData.type === 'food' ? '美食' : '美景' }}
        </el-tag>
      </el-form-item>

      <el-form-item label="所属区县">
        <el-input v-model="formData.districtName" disabled/>
      </el-form-item>

      <el-form-item label="打卡内容">
        <el-input v-model="formData.content" disabled type="textarea" :rows="3"/>
      </el-form-item>

      <el-form-item label="打卡图片">
        <el-image
            v-if="formData.images"
            :src="formData.images.split(',')[0]"
            :preview-src-list="formData.images.split(',')"
            style="width: 100px; height: 100px;"
        />
        <span v-else>无图片</span>
      </el-form-item>

      <el-form-item label="审核状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">通过</el-radio>
          <el-radio :label="2">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="拒绝原因" prop="rejectReason" v-if="formData.status === 2">
        <el-input v-model="formData.rejectReason" placeholder="请填写拒绝原因" type="textarea" :rows="3"/>
      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </template>

  </el-drawer>
</template>

<script setup lang="ts" name="CheckinReviewDrawer">
  import { ref, reactive, watch } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { clearEmptyValue } from '@/utils';

  interface DrawerProps {
    rowData?: any
    api?: (params: any) => Promise<any>
    getTableList?: () => Promise<any>
  }

  const rules = reactive({
    status: { required: true, message: '请选择审核状态', trigger: 'blur' },
    rejectReason: {
      required: true,
      message: '请填写拒绝原因',
      trigger: 'blur',
      validator: (rule: any, value: any, callback: any) => {
        if (formData.value.status === 2 && !value) {
          callback(new Error('请填写拒绝原因'));
        } else {
          callback();
        }
      }
    }
  });

  const ruleFormRef = ref<FormInstance>();
  // drawer框状态
  const drawerVisible = ref(false);
  const drawerProps = ref<DrawerProps>({});
  // 表单数据
  const formData = ref({
    id: '',
    title: '',
    type: '',
    districtName: '',
    content: '',
    images: '',
    status: 1,
    rejectReason: ''
  });
  // loading
  const loading = ref<boolean>(false);

  // 接收父组件传过来的参数
  const acceptParams = (params: DrawerProps): void => {
    drawerProps.value = params;
    // 填充表单数据
    formData.value = {
      id: params.rowData.id,
      title: params.rowData.title,
      type: params.rowData.type,
      districtName: params.rowData.districtName,
      content: params.rowData.content,
      images: params.rowData.images,
      status: 1,
      rejectReason: ''
    };
    drawerVisible.value = true;
  };

  const handleSubmit = () => {
    ruleFormRef.value!.validate(async (valid) => {
      if (!valid) return;
      try {
        loading.value = true;
        // 处理接口参数
        const { id, status, rejectReason } = formData.value;
        const params = {
          checkinId: id,
          status: status as 1 | 2,
          rejectReason: status === 2 ? rejectReason : undefined
        };
        await drawerProps.value.api!(clearEmptyValue(params));
        ElMessage.success({ message: '审核成功！' });
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