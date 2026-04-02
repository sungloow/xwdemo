<template>
  <div class="card hover:shadow-md hover:-translate-y-0.5 transition-all duration-200">
    <!-- Image -->
    <div v-if="firstImage" class="relative h-44 overflow-hidden bg-slate-100">
      <img :src="firstImage" :alt="record.title" class="w-full h-full object-cover" loading="lazy" />
      <div class="absolute top-3 left-3">
        <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium backdrop-blur-sm"
          :class="record.type === 'food' ? 'bg-amber-500/90 text-white' : 'bg-sky-500/90 text-white'">
          {{ record.type === 'food' ? '🍜 美食' : '🏞 美景' }}
        </span>
      </div>
      <div class="absolute top-3 right-3">
        <StatusBadge :status="record.status" />
      </div>
    </div>

    <div class="p-5 space-y-3">
      <!-- Status (no image) -->
      <div v-if="!firstImage" class="flex items-center justify-between">
        <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium"
          :class="record.type === 'food' ? 'bg-amber-100 text-amber-700' : 'bg-sky-100 text-sky-700'">
          {{ record.type === 'food' ? '🍜 美食' : '🏞 美景' }}
        </span>
        <StatusBadge :status="record.status" />
      </div>

      <!-- Title -->
      <h3 class="font-semibold text-slate-800 line-clamp-2 leading-snug">{{ record.title }}</h3>

      <!-- Content -->
      <p v-if="record.content" class="text-sm text-slate-500 line-clamp-2">{{ record.content }}</p>

      <!-- Meta -->
      <div class="flex flex-wrap gap-3 text-xs text-slate-400">
        <span v-if="record.districtName" class="flex items-center gap-1">
          <MapPinIcon class="w-3 h-3" />
          {{ record.districtName }}
        </span>
        <span v-if="record.foodTypeName || record.scenicSpotName" class="flex items-center gap-1">
          <TagIcon class="w-3 h-3" />
          {{ record.foodTypeName || record.scenicSpotName }}
        </span>
        <span class="flex items-center gap-1 ml-auto">
          <ClockIcon class="w-3 h-3" />
          {{ formatDate(record.createTime) }}
        </span>
      </div>

      <!-- Reject reason -->
      <div v-if="record.status === 2 && record.rejectReason" class="flex items-start gap-2 p-3 bg-red-50 border border-red-100 rounded-xl text-xs text-red-600">
        <AlertCircleIcon class="w-3.5 h-3.5 mt-0.5 shrink-0" />
        <span>拒绝原因：{{ record.rejectReason }}</span>
      </div>

      <!-- Image count badge -->
      <div v-if="imageCount > 1" class="flex items-center gap-1 text-xs text-slate-400">
        <ImageIcon class="w-3.5 h-3.5" />
        {{ imageCount }} 张图片
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { MapPin as MapPinIcon, Tag as TagIcon, Clock as ClockIcon, AlertCircle as AlertCircleIcon, Image as ImageIcon } from 'lucide-vue-next';
import type { CheckinRecord } from '@/api/checkin';
import StatusBadge from './StatusBadge.vue';

const props = defineProps<{ record: CheckinRecord }>();

const images = computed(() => props.record.images?.split(',').filter(Boolean) || []);
const firstImage = computed(() => images.value[0]);
const imageCount = computed(() => images.value.length);

function formatDate(dateStr: string) {
  try {
    const d = new Date(dateStr);
    const now = new Date();
    const diff = now.getTime() - d.getTime();
    const days = Math.floor(diff / 86400000);
    if (days === 0) return '今天';
    if (days === 1) return '昨天';
    if (days < 7) return `${days}天前`;
    return d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
  } catch {
    return dateStr;
  }
}
</script>
