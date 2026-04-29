<template>
  <div>
    <ProTable
        ref="proTable"
        :dataCallback="dataCallback"
        :columns="columns"
        :requestApi="getUserList"
        :toolButton="false"
        :searchCol="5"
    >
      <!-- 表头 -->
      <template #tableHeader="scope">
        <permission-btn
            permission="system:user:add"
            type="primary"
            @btnSubmit="openDrawer('新增')"
        />
        <permission-btn
            permission="system:user:remove"
            type="danger" plain
            :disabled="!scope.isSelected"
            @btnSubmit="batchDelete(scope.selectedListIds)"
        />
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <permission-btn
            permission="system:user:update"
            type="primary" link
            @btnSubmit="openDrawer('编辑', scope.row)"
        />
        <permission-btn
            v-if="scope.row.isSuper !== 1"
            permission="system:user:delete"
            type="primary" link
            @btnSubmit="handleDelete(scope.row)"
        />

        <permission-btn
            permission="system:user:reset"
            type="primary" link
            @btnSubmit="resetPassword(scope.row)"
        />
      </template>
    </ProTable>

    <UserDrawer ref="drawerRef"/>

  </div>
</template>

<script setup lang="ts" name="User">
  import { ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import { useHandleData } from '@/hooks/useHandleData';
  import { getUserList, updateUser, deleteUserBatch, addUser, getUserRoles, resetPasswordByAdmin, getUserDetail } from '@/apis';
  import { useAuthStore } from '@/store/auth';
  import UserDrawer from './UserDrawer.vue';

  const authStore: any = useAuthStore();

  // 获取 ProTable 元素，调用其数据方法
  const proTable = ref();
  // 获取新增编辑组件元素，调用其数据方法
  const drawerRef = ref();

  // *表格配置项
  const columns = [
    { type: 'selection', fixed: 'left', width: 80, selectable: ((row: any) => row.isSuper !== 1) },
    {
      prop: 'username', label: '用户名',
      search: { el: 'input', key: 'username', props: { placeholder: '输入用户名' } }
    },
    {
      prop: 'roleName', label: '角色',
      render: ({ row }: any) => row.isSuper === 1 ? '超级管理员' : row.roleName
    },
    { prop: 'email', label: '邮箱', search: { el: 'input', key: 'email', props: { placeholder: '输入邮箱' } } },
    { prop: 'phone', label: '联系方式', search: { el: 'input', key: 'phone', props: { placeholder: '输入联系方式' } } },
    { prop: 'operation', label: '操作', fixed: 'right', width: 280 },
  ];

  /**
   *处理返回的数据格式
   */
  const dataCallback = (data: any) => {
    return {
      list: data?.records,
      pageNumber: data.pageNumber,
      pageSize: data.pageSize,
      totalRow: data.totalRow
    };
  };

  /**
   * 新增、编辑用户信息以及分配角色
   * @param title
   * @param rowData
   */
  const openDrawer = async (title: string, rowData: any = {}) => {
    // 获取当前登录用户可分配的所有角色列表
    const uid = authStore?.authInfo?.id;
    const roleListRes: any = await getUserRoles(uid);
    // 新增编辑用户前先判断是否有可分配的角色，无角色需要先新建角色
    if ((roleListRes?.data ?? []).length === 0) {
      ElMessage.warning('新增用户需分配角色，请先创建角色后再进行操作！');
      return;
    }
    let user;
    if (title === '新增') {
      user = { ...rowData };
    } else {
      // 获取未脱敏的用户信息
      const res: any = await getUserDetail(rowData.id);
      user = res.data;
    }
    const params = {
      title,
      rowData: user,
      list: roleListRes?.data ?? [],
      api: title === '新增' ? addUser : updateUser,
      getTableList: proTable.value.getTableList,
    };
    drawerRef.value.acceptParams(params);
  };

  /**
   * 重置密码
   * @param row
   */
  const resetPassword = async (row: any) => {
    await useHandleData(resetPasswordByAdmin, {userId:row.id,newPassword:'123456user'},
      `重置${row.username}的密码`);
  };

  /**
   * 根据id删除用户
   * @param row
   */
  const handleDelete = async (row: any) => {
    // 超管不能删除
    if (row?.isSuper === 1) {
      ElMessage({
        type: 'warning',
        message: `系统用户不允许删除`,
      });
      return;
    }
    await useHandleData(deleteUserBatch, [row.id], `删除${row.username}用户`);
    proTable.value.getTableList();
  };

  // *批量删除用户
  const batchDelete = async (ids: string[]) => {
    await useHandleData(deleteUserBatch, ids, '删除所选用户信息');
    proTable.value.clearSelection();
    proTable.value.getTableList();
  };

</script>
