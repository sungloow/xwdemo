<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 py-10 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="space-y-1">
        <div class="flex items-center gap-2 text-sm text-slate-400 mb-1">
          <router-link to="/" class="hover:text-primary-600 transition-colors">首页</router-link>
          <span>/</span>
          <span class="text-slate-600">我的打卡</span>
        </div>
        <h1 class="text-2xl font-bold text-slate-900">我的打卡记录</h1>
        <p class="text-sm text-slate-500">共 <span class="font-semibold text-primary-600">{{ totalCount }}</span> 条记录</p>
      </div>
      <router-link to="/checkin/new" class="btn-primary">
        <PlusIcon class="w-4 h-4" />
        发起打卡
      </router-link>
    </div>

    <!-- Filter Tabs -->
    <div class="flex gap-2 p-1 bg-slate-100 rounded-2xl w-fit">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="switchTab(tab.value)"
        class="px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200"
        :class="activeTab === tab.value
          ? 'bg-white text-slate-800 shadow-sm'
          : 'text-slate-500 hover:text-slate-700'"
      >
        {{ tab.label }}
        <span v-if="tab.count !== undefined" class="ml-1.5 px-1.5 py-0.5 rounded-full text-xs"
          :class="activeTab === tab.value ? 'bg-primary-100 text-primary-600' : 'bg-slate-200 text-slate-400'">
          {{ tab.count }}
        </span>
      </button>
    </div>

    <!-- Type filter -->
    <div class="flex gap-2">
      <button
        v-for="t in typeFilters"
        :key="t.value"
        @click="activeType = t.value"
        class="px-3.5 py-1.5 rounded-xl text-sm font-medium border transition-all duration-150"
        :class="activeType === t.value
          ? 'bg-primary-600 text-white border-primary-600 shadow-sm'
          : 'bg-white text-slate-600 border-slate-200 hover:border-primary-300'"
      >
        {{ t.label }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <div class="flex flex-col items-center gap-3">
        <div class="w-10 h-10 rounded-full border-3 border-slate-200 border-t-primary-500 animate-spin" />
        <p class="text-sm text-slate-400">加载中...</p>
      </div>
    </div>

    <!-- Empty -->
    <div v-else-if="!records.length" class="flex flex-col items-center justify-center py-20 gap-4">
      <div class="w-20 h-20 rounded-2xl bg-slate-100 flex items-center justify-center text-4xl">
        {{ activeType === 'food' ? '🍜' : activeType === 'scenic' ? '🏞' : '📍' }}
      </div>
      <div class="text-center space-y-1">
        <p class="font-semibold text-slate-600">暂无打卡记录</p>
        <p class="text-sm text-slate-400">快去探索重庆的美食美景吧！</p>
      </div>
      <router-link to="/checkin/new" class="btn-primary mt-2">
        <PlusIcon class="w-4 h-4" />
        立即打卡
      </router-link>
    </div>

    <!-- Records Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-5">
      <CheckinRecordCard
        v-for="record in records"
        :key="record.id"
        :record="record"
      />
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 pt-4">
      <button
        @click="goPage(currentPage - 1)"
        :disabled="currentPage <= 1"
        class="w-9 h-9 flex items-center justify-center rounded-xl border border-slate-200 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
      >
        <ChevronLeftIcon class="w-4 h-4 text-slate-600" />
      </button>
      <div class="flex gap-1">
        <button
          v-for="p in pageRange"
          :key="p"
          @click="typeof p === 'number' && goPage(p)"
          class="w-9 h-9 flex items-center justify-center rounded-xl text-sm font-medium transition-all"
          :class="p === currentPage
            ? 'bg-primary-600 text-white shadow-sm'
            : typeof p === 'number' ? 'border border-slate-200 text-slate-600 hover:bg-slate-50' : 'text-slate-400 cursor-default'"
        >
          {{ p }}
        </button>
      </div>
      <button
        @click="goPage(currentPage + 1)"
        :disabled="currentPage >= totalPages"
        class="w-9 h-9 flex items-center justify-center rounded-xl border border-slate-200 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
      >
        <ChevronRightIcon class="w-4 h-4 text-slate-600" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { Plus as PlusIcon, ChevronLeft as ChevronLeftIcon, ChevronRight as ChevronRightIcon } from 'lucide-vue-next';
import { getMyCheckinList } from '@/api/checkin';
import type { CheckinRecord } from '@/api/checkin';
import CheckinRecordCard from '@/components/CheckinRecordCard.vue';

const loading = ref(false);
const records = ref<CheckinRecord[]>([]);
const totalCount = ref(0);
const currentPage = ref(1);
const pageSize = 8;

const activeTab = ref<string>('all');
const activeType = ref<string>('all');

const tabs = computed(() => [
  { label: '全部', value: 'all', count: totalCount.value },
  { label: '已发布', value: '1', count: undefined },
  { label: '审核中', value: '0', count: undefined },
  { label: '未通过', value: '2', count: undefined },
]);

const typeFilters = [
  { label: '全部类型', value: 'all' },
  { label: '🍜 美食', value: 'food' },
  { label: '🏞 美景', value: 'scenic' },
];

const totalPages = computed(() => Math.ceil(totalCount.value / pageSize));

const pageRange = computed(() => {
  const range: (number | '...')[] = [];
  const total = totalPages.value;
  const cur = currentPage.value;
  if (total <= 7) {
    for (let i = 1; i <= total; i++) range.push(i);
  } else {
    range.push(1);
    if (cur > 3) range.push('...');
    for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) range.push(i);
    if (cur < total - 2) range.push('...');
    range.push(total);
  }
  return range;
});

async function fetchRecords() {
  loading.value = true;
  try {
    const params: Record<string, any> = { current: currentPage.value, size: pageSize };
    if (activeTab.value !== 'all') params.status = Number(activeTab.value);
    if (activeType.value !== 'all') params.type = activeType.value;
    const res = await getMyCheckinList(params);
    records.value = res.data.records;
    totalCount.value = res.data.total;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function switchTab(val: string) {
  activeTab.value = val;
  currentPage.value = 1;
}

function goPage(p: number) {
  currentPage.value = p;
}

watch([activeTab, activeType, currentPage], fetchRecords);
onMounted(fetchRecords);
</script>
