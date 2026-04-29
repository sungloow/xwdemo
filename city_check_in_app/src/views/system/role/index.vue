<template>
  <div>
    <ProTable
        ref="proTable"
        :pagination="false"
        :toolButton="false"
        :columns="columns"
        :requestApi="getRoleList"
        :searchCol="4"
    >
      <!-- Expand -->
      <template #tableHeader="scope">
        <permission-btn
            permission="system:role:add"
            type="primary"
            @btnSubmit="openDrawer('新增')"
        />

        <!-- 批量删除 -->
        <permission-btn
            permission="system:role:remove"
            type="danger" plain
            @btnSubmit="batchDelete(scope.selectedListIds)"
        />
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <!-- 编辑 -->
        <permission-btn
            permission="system:role:update"
            type="primary" link
            @btnSubmit="openDrawer('编辑', scope.row)"
        />

        <!-- 删除 -->
        <permission-btn
            permission="system:role:delete"
            type="primary" link
            @btnSubmit="handleDelete(scope.row)"
        />
      </template>
    </ProTable>
    <RoleDrawer ref="drawerRef"/>
  </div>
</template>

<script setup lang="ts" name="Role">
  import { ref } from 'vue';
  import type { ColumnProps } from '@/components/ProTable/src/types';
  import { useHandleData } from '@/hooks/useHandleData';
  import { getRoleList, deleteRole, addRole, updateRole, getUserMenuTree, getRoleDetail } from '@/apis';
  import RoleDrawer from './RoleDrawer.vue';

  const columns: ColumnProps[] = [
    { type: 'selection', fixed: 'left', width: 100 },
    {
      prop: 'code', label: '角色编码',
      search: { el: 'input', props: { placeholder: '输入角色编码' } },
    },
    {
      prop: 'roleName', label: '角色名称',
      search: { el: 'input', props: { placeholder: '输入角色名称' } },
    },
    { prop: 'description', label: '角色描述' },
    { prop: 'creator', label: '创建人' },
    { prop: 'operation', label: '操作', fixed: 'right', width: 180 },
  ];

  const proTable = ref();
  // 打开Drawer
  const drawerRef = ref();

  /**
   * 新增编辑角色抽屉
   * @param title
   * @param rowData
   */
  const openDrawer = async (title: string, rowData: any = {}) => {
    let data = {};
    if (title === '编辑') {
      const res:any = await getRoleDetail(rowData.id);
      data = {data: res?.data.role, menuIds:res.data.menuIds};
    }
    const params = {
      title,
      rowData: data,
      // 该角色的权限菜单列表
      list: await getUserMenuTree(),
      api: title === '新增' ? addRole : updateRole,
      getTableList: proTable.value.getTableList,
    };
    drawerRef.value.acceptParams(params);
  };

  // *根据id删除角色
  const handleDelete = async (row: any) => {
    await useHandleData(deleteRole, [row.id], `删除${row.roleName}角色`);
    proTable.value.getTableList();
  };

  // *批量删除角色
  const batchDelete = async (ids: string[]) => {
    await useHandleData(deleteRole, ids, '删除所选角色信息');
    proTable.value.clearSelection();
    proTable.value.getTableList();
  };

</script>
