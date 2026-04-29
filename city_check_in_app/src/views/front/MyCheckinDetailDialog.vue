<template>
  <el-dialog
    v-model="visible"
    width="780px"
    :align-center="true"
    title="打卡详情"
    class="my-checkin-detail-dialog"
  >
    <div v-loading="loading" class="detail-wrap">
      <template v-if="detail">
        <div class="detail-head">
          <div>
            <h2>{{ detail.title || '未命名打卡' }}</h2>
            <div class="detail-tags">
              <el-tag :type="detail.type === 'food' ? 'warning' : 'primary'">
                {{ detail.type === 'food' ? '美食' : '美景' }}
              </el-tag>
              <el-tag :type="statusMeta.type">{{ statusMeta.label }}</el-tag>
              <el-tag v-if="detail.districtName" type="info">{{ detail.districtName }}</el-tag>
            </div>
          </div>
        </div>

        <div v-if="images.length" class="image-strip">
          <el-image
            v-for="image in images"
            :key="image"
            :src="image"
            :preview-src-list="images"
            fit="cover"
          />
        </div>

        <dl class="detail-list">
          <div>
            <dt>类型明细</dt>
            <dd>{{ detail.foodTypeName || detail.scenicSpotName || '未填写' }}</dd>
          </div>
          <div>
            <dt>打卡地址</dt>
            <dd>{{ detail.address || '未填写' }}</dd>
            <CheckinLocationMap
              :latitude="detail.latitude"
              :longitude="detail.longitude"
              :address="detail.address"
              :title="detail.title"
            />
          </div>
          <div>
            <dt>打卡内容</dt>
            <dd>{{ detail.content || '未填写' }}</dd>
          </div>
          <div v-if="detail.status === 2">
            <dt>拒绝原因</dt>
            <dd class="reject-text">{{ detail.rejectReason || '管理员未填写原因' }}</dd>
          </div>
        </dl>
      </template>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button
        v-if="canEdit"
        type="primary"
        @click="handleEdit"
      >
        编辑后重新提交
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { CheckinDetail } from '@/apis/checkin';
  import { getMyCheckinDetail } from '@/apis';
  import CheckinLocationMap from '@/views/front/CheckinLocationMap.vue';

  const emit = defineEmits<{
    (e: 'edit', detail: CheckinDetail): void
  }>();

  const visible = ref(false);
  const loading = ref(false);
  const detail = ref<CheckinDetail | null>(null);

  const images = computed(() => {
    return (detail.value?.images || '').split(',').map((item) => item.trim()).filter(Boolean);
  });

  const statusMeta = computed(() => {
    if (detail.value?.status === 0) return { label: '待审核', type: 'info' };
    if (detail.value?.status === 1) return { label: '已通过', type: 'success' };
    return { label: '已拒绝', type: 'danger' };
  });

  const canEdit = computed(() => detail.value?.status === 0 || detail.value?.status === 2);

  const open = async (id: number) => {
    visible.value = true;
    loading.value = true;
    try {
      const res: any = await getMyCheckinDetail(id);
      detail.value = res.data;
    } finally {
      loading.value = false;
    }
  };

  const handleEdit = async () => {
    if (!detail.value) return;
    const res: any = await getMyCheckinDetail(detail.value.id);
    detail.value = res.data;
    if (detail.value?.status === 1) {
      ElMessage.warning('该打卡已审核通过，不能继续编辑');
      return;
    }
    emit('edit', detail.value);
    visible.value = false;
  };

  defineExpose({ open });
</script>

<style scoped lang="scss">
  .detail-wrap {
    min-height: 220px;
  }

  .detail-head {
    display: flex;
    justify-content: space-between;
    gap: 16px;

    h2 {
      margin: 0;
      color: #111827;
      font-size: 22px;
      line-height: 1.35;
    }
  }

  .detail-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 12px;
  }

  .image-strip {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 10px;
    margin-top: 18px;

    :deep(.el-image) {
      width: 100%;
      aspect-ratio: 1 / 1;
      border-radius: 8px;
      overflow: hidden;
      background: #f3f4f6;
    }
  }

  .detail-list {
    margin: 18px 0 0;

    div {
      padding: 14px 0;
      border-top: 1px solid #eef2f7;
    }

    dt {
      margin-bottom: 6px;
      color: #64748b;
      font-size: 13px;
      font-weight: 700;
    }

    dd {
      margin: 0;
      color: #111827;
      line-height: 1.7;
      white-space: pre-wrap;
    }
  }

  .reject-text {
    color: #b91c1c;
  }

  @media (max-width: 720px) {
    .image-strip {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
</style>
