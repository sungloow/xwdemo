<template>
  <div class="card p-5 hover:shadow-md hover:-translate-y-0.5 transition-all duration-200">
    <div class="flex items-center gap-4">
      <div
        class="w-12 h-12 rounded-2xl flex items-center justify-center text-xl shrink-0 font-bold"
        :class="rank <= 3 ? rankBg : 'bg-slate-100 text-slate-500 text-sm'"
      >
        {{ rank <= 3 ? ['🥇','🥈','🥉'][rank-1] : rank }}
      </div>
      <div class="flex-1 min-w-0 space-y-1.5">
        <div class="flex items-start justify-between gap-2">
          <div class="min-w-0">
            <div class="font-semibold text-slate-800 truncate">{{ name }}</div>
            <div v-if="district" class="flex items-center gap-1 text-xs text-slate-400 mt-0.5">
              <MapPinIcon class="w-3 h-3" />
              {{ district }}
            </div>
          </div>
          <div class="shrink-0 text-right">
            <div class="text-lg font-bold text-sky-600">{{ count }}</div>
            <div class="text-xs text-slate-400">次打卡</div>
          </div>
        </div>
        <div class="h-1.5 bg-slate-100 rounded-full overflow-hidden">
          <div
            class="h-full bg-gradient-to-r from-sky-400 to-blue-500 rounded-full transition-all duration-700"
            :style="{ width: `${Math.round((count / maxCount) * 100)}%` }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { MapPin as MapPinIcon } from 'lucide-vue-next';

const props = defineProps<{
  rank: number;
  name: string;
  count: number;
  maxCount: number;
  district?: string;
}>();

const rankBg = computed(() => {
  const map = ['bg-amber-50', 'bg-slate-100', 'bg-orange-50'];
  return map[props.rank - 1] ?? '';
});
</script>
