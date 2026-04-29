<template>
  <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="450px"
      :title="`${drawerProps.title}`"
  >
    <el-form ref="ruleFormRef" label-width="100px" label-suffix=" :" :rules="rules" :model="drawerProps.rowData">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
            v-model="drawerProps.rowData.roleName"
            placeholder="请填写角色名称"
            clearable
        />
      </el-form-item>
      <el-form-item label="角色码" prop="code">
        <el-input
            v-model="drawerProps.rowData.code"
            placeholder="请填写角色码"
            clearable
        />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
            type="textarea"
            v-model="drawerProps.rowData.description"
            placeholder="请填写角色描述"
            clearable
        />
      </el-form-item>
      <el-form-item label="权限列表" prop="permissionIds">
        <div class="tree-border">
          <div class="tip">
            <el-icon :size="12">
              <Warning/>
            </el-icon>
            <span style="color: #ffa91b; font-size: 12px">温馨提示：请勾选上首页模块</span>
          </div>
          <el-tree
              ref="treeRef"
              node-key="id"
              check-strictly
              show-checkbox
              :default-checked-keys="defaultCheckedKeys"
              :data="allPermission"
              :props="defaultProps"
              @check="clickDeal"
          />
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        确定
      </el-button>
    </template>
  </el-drawer>
</template>

<script setup lang="ts" name="RoleDrawer">
  import { reactive, ref } from 'vue';
  import { ElMessage, ElTree } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { clearEmptyValue, textRule } from '@/utils';

  interface DrawerProps {
    title: string
    rowData?: any
    list?: any
    api?: (params: any) => Promise<any>
    getTableList?: () => Promise<any>
  }

  // drawer框状态
  const drawerVisible = ref(false);
  const drawerProps = ref<DrawerProps>({ title: '' });
  const defaultProps = { children: 'children', label: 'name' };
  const allPermission = ref<any[]>([]);
  const defaultCheckedKeys = ref<string[]>([]);
  const loading = ref<boolean>(false);
  const treeRef: any = ref<InstanceType<typeof ElTree>>();
  const roleList = ref([]);
  // 一级菜单id数组
  const firstLevelMenuIds: any = ref([]);
  // 角色表单
  const ruleFormRef = ref<FormInstance>();

  const validatorPermission = (rule: any, value: any, callback: any) => {
    const menuIds = treeRef.value?.getCheckedKeys();
    if (menuIds.length === 0) {
      callback(new Error('菜单权限不能为空'));
    }
    callback();
  };

  const rules = reactive({
    roleType: { required: true, message: '角色类型不能为空', trigger: 'blur' },
    roleName: {
      required: true, validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 20),
      trigger: 'blur'
    },
    code: {
      required: true,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 20),
      trigger: 'blur'
    },
    description: {
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 100),
      trigger: 'blur'
    },
    permissionIds: { required: true, validator: validatorPermission, trigger: 'blur' },
  });

  // 接收父组件传过来的参数
  const acceptParams = (params: DrawerProps): void => {
    const { list, rowData } = params;
    drawerProps.value = params;
    drawerVisible.value = true;
    allPermission.value = list.data;
    defaultCheckedKeys.value = rowData?.menuIds ?? [];
    list.data.forEach((item: any) => {
      if (item.pid === 0) {
        firstLevelMenuIds.value.push(item?.id);
      }
    });
  };

  // 统一处理子节点为相同的勾选状态
  const uniteChildSame = (nodeObj: any, isSelected: any) => {
    treeRef.value.setChecked(nodeObj.id, isSelected, false);
    for (let i = 0; i < nodeObj.children.length; i++) {
      uniteChildSame(nodeObj.children[i], isSelected);
    }
  };

  // 统一处理父节点为选中
  const selectedParent = (currentObj: any) => {
    const currentNode = treeRef.value.getNode(currentObj);
    if (currentNode.parent.key !== undefined) {
      treeRef.value.setChecked(currentNode.parent, true, false);
      selectedParent(currentNode.parent);
    }
  };

  // 	点击节点复选框之后触发 共两个参数，依次为：传递给 data 属性的数组中该节点所对应的对象、树目前的选中状态对象
  const clickDeal = (currentObj: any, treeStatus: any) => {
    // 用于：父子节点严格互不关联时，父节点勾选变化时通知子节点同步变化，实现单向关联。
    let selected = treeStatus.checkedKeys.indexOf(currentObj.id);
    // 选中 -1未选中
    if (selected !== -1) {
      // 子节点只要被选中父节点就被选中
      selectedParent(currentObj);
      // 统一处理子节点为相同的勾选状态
      uniteChildSame(currentObj, true);
    } else {
      // 未选中 处理子节点全部未选中
      if (currentObj.children.length !== 0) {
        uniteChildSame(currentObj, false);
      }
      // 如果当前取消的节点的父节点是一级目录，且父节点的所有子节点都未选中，则处理父节点为未选中
      if (firstLevelMenuIds.value.includes(currentObj.pid)) {
        const parentObj = allPermission.value.find((item) => item.id === currentObj.pid);
        // 是否有二级菜单被选中
        const isSubMenuSelected = parentObj.children.some((item: any) => treeStatus.checkedKeys.includes(item.id));
        // 处理父节点为未选中
        if (!isSubMenuSelected) {
          treeRef.value.setChecked(parentObj.id, false, false);
        }
      }
    }
  };

  /**
   * 新增、编辑 事件
   */
  const handleSubmit = async () => {
    ruleFormRef.value!.validate(async (valid) => {
      if (!valid) return;
      try {
        // 获取selectdKeys
        const checkedKeys = treeRef.value?.getCheckedKeys() || [];
        const params = {
          id: drawerProps.value.rowData.id,
          code: drawerProps.value.rowData.code,
          roleName: drawerProps.value.rowData.roleName,
          description: drawerProps.value.rowData.description,
          menuIds: checkedKeys,
          roleType: drawerProps.value.rowData.roleType,
        };
        loading.value = true;
        await drawerProps.value.api!(clearEmptyValue(params));
        ElMessage.success({ message: `${drawerProps.value.title}成功！` });
        drawerProps.value.getTableList!();
        drawerVisible.value = false;
        // 角色分配成功，刷新当前页面
        // window.location.reload();
      } finally {
        loading.value = false;
      }
    });
  };

  defineExpose({
    acceptParams,
  });
</script>

<style lang="scss" scoped>
  .tree-border {
    width: 100%;
    padding: 0 20px 10px 20px;
    overflow-y: auto;
    border: 1px solid #dcdfe6;
    border-radius: 2.5px;

    .tip {
      line-height: 20px;
      color: #ffa91b;

      .el-icon {
        margin-right: 5px;
      }
    }
  }
</style>
