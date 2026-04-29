<template>
  <el-dialog
      v-model="dialogVisible"
      :title="title"
      :destroy-on-close="true"
      width="500px"
  >
    <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="permissionData">

      <el-form-item label="类型" prop="type" v-if="shouldDisplayFields('type')">
        <el-select v-model="permissionData.type" placeholder="请选择类型"
                   :disabled="Number(permissionData.level) === 3 || permissionData.pid === 0">
          <el-option :key="1" label="菜单" value="menu"/>
          <el-option :key="2" label="按钮" value="button" v-if="Number(permissionData.level) !== 2"/>
          <el-option :key="3" label="路由" value="router"/>
        </el-select>
      </el-form-item>

      <el-form-item label="名称" prop="name" v-if="shouldDisplayFields('name')">
        <el-input v-model="permissionData.name" placeholder="请填写名称" clearable/>
      </el-form-item>

      <el-form-item label="权限值" prop="code" v-if="shouldDisplayFields('code')">
        <el-input
            v-model="permissionData.code"
            placeholder="请填写权限值"
            :disabled="dialogProps.apiType!==1"
            clearable/>
      </el-form-item>

      <el-form-item label="排序" prop="sortOrder" v-if="shouldDisplayFields('sortOrder')">
        <el-input-number v-model="permissionData.sortOrder" :min="1"/>
      </el-form-item>

      <el-form-item label="图标" prop="icon" v-if="shouldDisplayFields('icon')">
        <ElIconPicker v-model="permissionData.icon"/>
      </el-form-item>

      <el-form-item label="路由路径" prop="path" v-if="shouldDisplayFields('path')">
        <el-input
            v-model="permissionData.path"
            placeholder="请填写路由"
            clearable
        />
      </el-form-item>

      <el-form-item label="组件路径" prop="component" v-if="shouldDisplayFields('component')">
        <el-input
            v-model="permissionData.component"
            placeholder="请填写组件相对路径"
            clearable
        />
      </el-form-item>

      <el-form-item label="活动菜单" prop="activeMenu" v-if="shouldDisplayFields('activeMenu')">
        <el-input
            v-model="permissionData.activeMenu"
            placeholder="当前路由在左侧选中的菜单，默认不填"
            clearable
        />
      </el-form-item>

      <el-form-item label="重定向路径" prop="redirect" v-if="shouldDisplayFields('redirect')">
        <el-input
            v-model="permissionData.redirect"
            placeholder="请填写重定向路径"
            clearable
        />
      </el-form-item>

      <el-form-item label="固定菜单" prop="affix" v-if="shouldDisplayFields('affix')">
        <el-select v-model="permissionData.affix" placeholder="请选择是否固定菜单">
          <el-option :key="0" label="不固定" :value="0"/>
          <el-option :key="1" label="固定" :value="1"/>
        </el-select>
      </el-form-item>

    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit(ruleFormRef)">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
  import { ref, reactive } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { getDialogTitle } from './helper';
  import { useUserStore } from '@/store/user';
  import { menuCodeRule, textRule } from '@/utils';

  interface DialogProps {
    apiType: number
    rowData?: any
    api?: (params: any) => Promise<any>
    getTableList?: () => Promise<any>
  }

  const userStore = useUserStore();

  const ruleFormRef = ref<FormInstance>();

  let permissionData = ref<any>({});

  const rules = reactive({
    name: [{
      required: true,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 20),
      trigger: 'blur'
    }],
    code: { required: true, validator: menuCodeRule, trigger: 'blur' },
    path: [{ required: true, message: '请填写路由' }],
    component: [{
      required: true,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 100),
      trigger: 'blur'
    }]
  });

  const dialogVisible = ref(false);
  // dialog标题
  const title = ref<string>();
  const loading = ref<boolean>(false);
  // props定义
  const dialogProps = ref<DialogProps>({ apiType: 1 });

  // 初始化 接收父组件参数
  const acceptParams = (params: DialogProps): void => {
    const row: any = params.rowData;
    if (params.apiType === 1) {
      // 新增
      permissionData.value = {
        pid: row.id,
        level: Number(row.level) + 1,
        type: Number(row.level) == 2 ? 'button' : 'menu',
        sortOrder: 1,
        // 默认展示
        isShow: 1,
        // 布局组件路径 一级菜单默认
        component: row.id === 0 ? 'layouts/index' : ''
      };
    } else {
      // 编辑
      permissionData.value = row;
    }
    title.value = getDialogTitle(params);
    dialogProps.value = params;
    dialogVisible.value = true;
  };

  /**
   * 控制表单展示哪些字段
   */
  const shouldDisplayFields = (prop: string) => {
    // 一级只有菜单 只展示类型、名称、权限值、排序、图标、组件路径
    if (permissionData.value.level === 1 || permissionData.value.pid === 0) {
      return ['type', 'name', 'code', 'sortOrder', 'icon', 'component'].includes(prop);
    }
    // 二级只有菜单/路由(不展示在左边)  展示所有字段
    else if (permissionData.value.level === 2) {
      return true;
    }
    // 三级 按钮+路由
    else if (permissionData.value.level === 3) {
      if (permissionData.value.type === 'button') {
        // 按钮：  类型 名称 权限值 图标
        return ['type', 'name', 'code', 'icon'].includes(prop);
      } else {
        // 路由
        return true;
      }
    }
    return false;
  };

  /**
   * 确认按钮
   * @param formEl
   */
  const handleSubmit = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    formEl.validate(async (valid) => {
      if (!valid) return;
      try {
        loading.value = true;
        await dialogProps.value.api!(permissionData.value);
        ElMessage.success({ message: `${title.value}成功！` });
        dialogProps.value.getTableList!();
        dialogVisible.value = false;
        loading.value = false;
        // 重置表单
        resetForm(formEl);
        // 刷新菜单
        await userStore.GetInfoAction();
      } catch (error) {
        loading.value = false;
      }
    });
  };

  /**
   * 重置表单
   * @param formEl
   */
  const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    formEl.resetFields();
  };

  // 暴露给父组件的方法
  defineExpose({ acceptParams });

</script>

<style lang="scss" scoped></style>
