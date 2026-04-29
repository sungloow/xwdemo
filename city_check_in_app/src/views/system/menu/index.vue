<template>
  <div>
    <ProTable
        ref="proTable"
        :pagination="false"
        :toolButton="false"
        :columns="columns"
        :requestApi="getTreeList"
    >
      <!-- Expand -->
      <template #tableHeader>
        <permission-btn
            permission="system:menu:add"
            type="primary"
            @btnSubmit="openDialog(1, defaultParam)"
        />
      </template>

      <!-- 表格操作 -->
      <template #show="scope">
        <!-- 是否可见-->
        <el-switch
            v-permission="['system:menu:show']"
            :model-value="scope.row.show"
            inline-prompt
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
            :before-change="beforeSwitchChange"
            @change="handleShowState(scope.row)"
        />
      </template>

      <template #operation="scope">
        <permission-btn
            v-if="Number(scope.row.level) !== 3"
            permission="system:menu:add"
            type="primary" link
            @btnSubmit="openDialog(1, scope.row)"
        />
        <permission-btn
            permission="system:menu:update"
            type="primary" link
            @btnSubmit="openDialog(2, scope.row)"
        />
      </template>

    </ProTable>
    <PermissonDialog ref="DialogRef"/>
  </div>
</template>

<script setup lang="ts" name="Menu">
  import { ref, reactive } from 'vue';
  import { ElMessage } from 'element-plus';
  import { getUserMenuTree, updateMenu, addMenu } from '@/apis';
  import { useUserStore } from '@/store/user';
  import PermissonDialog from './PermissionDialog.vue';

  // 新增一级菜单的默认参数
  const defaultParam = { id: 0, order: 1, type: 'menu', name: '', code: '', icon: 'Folder', level: 0 };

  const columns = [
    { prop: 'name', label: '名称', align: 'left', width: '140px' },
    {
      prop: 'type', label: '类型', width: 100,
      render: ({ row }: any) => row.type === 'menu' ? '菜单' : row.type === 'router' ? '路由' : '按钮'
    },
    { prop: 'code', label: '权限值', width: '200px' },
    { prop: 'icon', label: '图标', width: '100px' },
    { prop: 'path', label: '路由路径', width: '200px' },
    { prop: 'component', label: '组件路径', width: '150px' },
    // { prop: 'activeMenu', label: '活动菜单', width: '80px' },
    { prop: 'sortOrder', label: '排序', width: 60 },
    // { prop: 'affix', label: '固定', width: 60,  render: ({ row }: any) => row.affix === 1 ? '是' : '否' },
    { prop: 'isShow', label: '是否展示', width: 100,
      render: ({ row }: any) => row.isShow === 1 ? '是' : '否'},
    { prop: 'operation', label: '操作', fixed: 'right', width: 180 },

  ];

  // *获取 ProTable 元素，调用其获取刷新数据方法
  const proTable = ref();
  // 打开Dialog
  const DialogRef = ref();

  const userStore = useUserStore();

  /**
   * 操作菜单
   * @param apiType 1-新增  2-编辑
   * @param rowData 单条菜单数据
   */
  const openDialog = (apiType: number, rowData: any) => {
    const params = {
      apiType,
      rowData: { ...rowData },
      api: apiType == 1 ? addMenu : updateMenu,
      getTableList: proTable.value.getTableList,
    };
    DialogRef.value.acceptParams(params);
  };

  const switchState = reactive({ switchStatus: false });

  const beforeSwitchChange = () => {
    switchState.switchStatus = true;
    return switchState.switchStatus;
  };

  /**
   * 切换开关 是否可见
   * @param row
   */
  const handleShowState = async (row: any) => {
    if (!switchState.switchStatus) {
      return;
    }
    try {
      await updateMenu({ ...row, show: row.show === 1 ? 0 : 1 });
    } catch (e) {
      ElMessage.error('操作失败，请重试！');
    } finally {
      // 刷新
      proTable.value.getTableList();
      await userStore.GetInfoAction();
    }
  };

  /**
   * 获取菜单列表
   */
  const getTreeList = async () => {
    return await getUserMenuTree();
  };

</script>
