<template>
  <main class="container-page py-10 space-y-6">
    <div class="flex items-start justify-between gap-4">
      <div>
        <div class="text-sm text-slate-500">
          <router-link to="/" class="hover:text-cyan-700">首页</router-link>
          <span class="mx-2">/</span>
          <span class="text-slate-700">我的打卡</span>
        </div>
        <h1 class="mt-2 text-2xl font-bold text-slate-900 tracking-tight">我的打卡记录</h1>
        <p class="mt-1 text-sm text-slate-600">
          共 <span class="font-semibold text-cyan-700 tabular-nums">{{ totalCount }}</span> 条
        </p>
      </div>
      <router-link to="/checkin/new" class="btn-primary">发起打卡</router-link>
    </div>

    <div class="card p-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
      <div class="flex flex-wrap gap-2">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          type="button"
          class="tab"
          :class="activeTab === tab.value ? 'tab-active' : 'tab-idle'"
          @click="switchTab(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>

      <div class="flex gap-2">
        <button
          v-for="t in typeFilters"
          :key="t.value"
          type="button"
          class="chip"
          :class="activeType === t.value ? 'chip-active' : 'chip-idle'"
          @click="activeType = t.value"
        >
          {{ t.label }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-14">
      <div class="flex items-center gap-2 text-slate-500">
        <span class="w-5 h-5 rounded-full border-2 border-slate-200 border-t-cyan-600 animate-spin" />
        加载中...
      </div>
    </div>

    <div v-else-if="!records.length" class="card p-10 text-center">
      <div class="text-4xl">📍</div>
      <div class="mt-2 font-semibold text-slate-800">暂无打卡记录</div>
      <div class="mt-1 text-sm text-slate-600">快去探索重庆的美食美景吧。</div>
      <router-link to="/checkin/new" class="btn-primary mt-5">立即打卡</router-link>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <article v-for="r in records" :key="r.id" class="card p-5">
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0">
            <div class="flex items-center gap-2">
              <span class="text-sm font-semibold text-slate-900 truncate">{{ r.title }}</span>
              <span class="badge" :class="statusClass(r.status)">{{ statusText(r.status) }}</span>
            </div>
            <div class="mt-1 text-xs text-slate-500">
              <span class="tabular-nums">{{ r.createTime }}</span>
              <span class="mx-2">·</span>
              <span>{{ r.type === 'food' ? '美食' : '美景' }}</span>
              <span v-if="r.districtName" class="mx-2">·</span>
              <span v-if="r.districtName">{{ r.districtName }}</span>
            </div>
          </div>
          <div class="w-10 h-10 rounded-2xl bg-white/60 border border-white/50 flex items-center justify-center shrink-0">
            <component :is="r.type === 'food' ? UtensilsIcon : ImageIcon" class="w-5 h-5 text-cyan-700" />
          </div>
        </div>

        <p v-if="r.content" class="mt-3 text-sm text-slate-600 clamp-3">{{ r.content }}</p>

        <div v-if="r.rejectReason && r.status === 2" class="mt-3 rounded-2xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
          未通过原因：{{ r.rejectReason }}
        </div>

        <div v-if="firstImage(r.images)" class="mt-4 rounded-2xl overflow-hidden border border-white/50 bg-white/60">
          <img :src="firstImage(r.images)!" class="w-full h-44 object-cover" alt="打卡图片" />
        </div>
      </article>
    </div>

    <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 pt-2">
      <button class="page-btn" :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">
        <ChevronLeftIcon class="w-4 h-4" />
      </button>
      <div class="flex gap-1">
        <button
          v-for="p in pageRange"
          :key="String(p)"
          class="page-num"
          :class="p === currentPage ? 'page-num-active' : typeof p === 'number' ? 'page-num-idle' : 'page-num-ellipsis'"
          :disabled="p === '...'"
          @click="typeof p === 'number' && goPage(p)"
        >
          {{ p }}
        </button>
      </div>
      <button class="page-btn" :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">
        <ChevronRightIcon class="w-4 h-4" />
      </button>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { getMyCheckinList } from '@/api/checkin';
import type { CheckinRecord } from '@/api/checkin';
import { ChevronLeft as ChevronLeftIcon, ChevronRight as ChevronRightIcon, Image as ImageIcon, Utensils as UtensilsIcon } from 'lucide-vue-next';

const loading = ref(false);
const records = ref<CheckinRecord[]>([]);
const totalCount = ref(0);
const currentPage = ref(1);
const pageSize = 8;

const activeTab = ref<'all' | '0' | '1' | '2'>('all');
const activeType = ref<'all' | 'food' | 'scenic'>('all');

const tabs = computed(() => [
  { label: '全部', value: 'all' as const },
  { label: '审核中', value: '0' as const },
  { label: '已发布', value: '1' as const },
  { label: '未通过', value: '2' as const },
]);

const typeFilters = [
  { label: '全部类型', value: 'all' as const },
  { label: '🍜 美食', value: 'food' as const },
  { label: '🏞 美景', value: 'scenic' as const },
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

function statusText(status: 0 | 1 | 2) {
  if (status === 1) return '已发布';
  if (status === 0) return '审核中';
  return '未通过';
}

function statusClass(status: 0 | 1 | 2) {
  if (status === 1) return 'badge-green';
  if (status === 0) return 'badge-amber';
  return 'badge-red';
}

function firstImage(images?: string) {
  if (!images) return null;
  const first = images.split(',').map((s) => s.trim()).filter(Boolean)[0];
  return first || null;
}

async function fetchRecords() {
  loading.value = true;
  try {
    const params: Record<string, any> = { current: currentPage.value, size: pageSize };
    if (activeTab.value !== 'all') params.status = Number(activeTab.value);
    if (activeType.value !== 'all') params.type = activeType.value;
    const res = await getMyCheckinList(params);
    records.value = res.data.records;
    totalCount.value = res.data.total;
  } finally {
    loading.value = false;
  }
}

function switchTab(val: typeof activeTab.value) {
  activeTab.value = val;
  currentPage.value = 1;
}

function goPage(p: number) {
  currentPage.value = p;
}

watch([activeTab, activeType, currentPage], fetchRecords);
onMounted(fetchRecords);
</script>

<style scoped>
.tab {
  @apply rounded-xl px-3.5 py-2 text-sm font-medium transition-colors;
}
.tab-idle {
  @apply bg-white/50 border border-white/50 text-slate-600 hover:bg-white/70;
}
.tab-active {
  @apply bg-cyan-600 text-white shadow-sm;
}
.chip {
  @apply rounded-xl px-3 py-2 text-sm font-medium border transition-colors;
}
.chip-idle {
  @apply bg-white/50 border-white/50 text-slate-700 hover:bg-white/70;
}
.chip-active {
  @apply bg-white border-white text-cyan-700 shadow-sm;
}
.badge {
  @apply inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold border;
}
.badge-green {
  @apply bg-emerald-50 text-emerald-800 border-emerald-200;
}
.badge-amber {
  @apply bg-amber-50 text-amber-900 border-amber-200;
}
.badge-red {
  @apply bg-red-50 text-red-700 border-red-200;
}
.page-btn {
  @apply w-9 h-9 rounded-xl border border-white/50 bg-white/50 hover:bg-white/70 transition-colors disabled:opacity-40 disabled:cursor-not-allowed flex items-center justify-center;
}
.page-num {
  @apply w-9 h-9 rounded-xl text-sm font-medium transition-colors;
}
.page-num-idle {
  @apply border border-white/50 bg-white/50 hover:bg-white/70 text-slate-700;
}
.page-num-active {
  @apply bg-cyan-600 text-white shadow-sm;
}
.page-num-ellipsis {
  @apply text-slate-400 cursor-default;
}
.clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

