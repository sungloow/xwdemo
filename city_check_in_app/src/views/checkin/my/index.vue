<template>
  <div>
    <div class="checkin-header">
      <el-button
          type="primary"
          @click="openAddDrawer"
      >
        发起打卡
      </el-button>
    </div>

    <ProTable
        ref="proTable"
        :dataCallback="dataCallback"
        :columns="columns"
        :requestApi="getMyCheckinList"
        :toolButton="false"
        :searchCol="5"
    >
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
        <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
        <el-tag v-else type="danger">已拒绝</el-tag>
      </template>

      <!-- 状态列 -->
      <template #status="scope">
        <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
        <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
        <el-tag v-else type="danger">已拒绝</el-tag>
      </template>

      <!-- 类型列 -->
      <template #type="scope">
        <el-tag v-if="scope.row.type === 'food'" type="warning">美食</el-tag>
        <el-tag v-else type="primary">美景</el-tag>
      </template>

      <!-- 图片列 -->
      <template #images="scope">
        <el-image
            v-if="scope.row.images"
            :src="scope.row.images.split(',')[0]"
            :preview-src-list="scope.row.images.split(',')"
            style="width: 60px; height: 60px;"
        />
        <span v-else>无图片</span>
      </template>

    </ProTable>

    <AddCheckinDrawer ref="addDrawerRef"/>

  </div>
</template>

<script setup lang="ts" name="MyCheckin">
  import { ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import { getMyCheckinList } from '@/apis';
  import AddCheckinDrawer from './AddCheckinDrawer.vue';

  // 获取 ProTable 元素，调用其数据方法
  const proTable = ref();
  // 获取新增打卡组件元素，调用其数据方法
  const addDrawerRef = ref();

  /**
   *处理返回的数据格式
   */
  const dataCallback = (data: any) => {
    return {
      list: data?.records,
      pageNumber: data.current,
      pageSize: data.size,
      totalRow: data.total
    };
  };

  // *表格配置项
  const columns = [
    {
      prop: 'title', label: '打卡标题',
      search: { el: 'input', key: 'title', props: { placeholder: '输入打卡标题' } }
    },
    {
      prop: 'type', label: '类型',
      enum: [
        { label: '美食', value: 'food' },
        { label: '美景', value: 'scenic' }
      ],
      search: {
        el: 'select',
        key: 'type',
        props: {
          placeholder: '选择类型'
        }
      }
    },
    {
      prop: 'status', label: '审核状态',
      enum: [
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已拒绝', value: 2 }
      ],
      search: {
        el: 'select',
        key: 'status',
        props: {
          placeholder: '选择审核状态'
        }
      }
    },
    { prop: 'content', label: '打卡内容', width: 200 },
    { prop: 'images', label: '打卡图片', width: 100 },
    { prop: 'address', label: '打卡地址' },
    { prop: 'createTime', label: '创建时间', width: 180 },
    { prop: 'operation', label: '状态', fixed: 'right', width: 100 },
  ];

  /**
   * 打开新增打卡抽屉
   */
  const openAddDrawer = () => {
    const params = {
      title: '发起打卡',
      api: '',
      getTableList: proTable.value.getTableList,
    };
    addDrawerRef.value.acceptParams(params);
  };

</script>

<style lang="scss" scoped>
  .checkin-header {
    margin-bottom: 15px;
    display: flex;
    justify-content: flex-end;
  }
</style>
