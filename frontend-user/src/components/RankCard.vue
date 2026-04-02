<template>
  <div class="card p-5 hover:shadow-md hover:-translate-y-0.5 transition-all duration-200 group">
    <div class="flex items-center gap-4">
      <!-- Rank badge -->
      <div class="shrink-0">
        <div
          v-if="rank <= 3"
          class="w-10 h-10 rounded-xl flex items-center justify-center text-xl font-bold shadow-sm"
          :class="medalClass"
        >
          {{ medalEmoji }}
        </div>
        <div v-else class="w-10 h-10 rounded-xl bg-slate-100 flex items-center justify-center text-sm font-bold text-slate-500">
          {{ rank }}
        </div>
      </div>

      <!-- Name & count -->
      <div class="flex-1 min-w-0">
        <div class="flex items-center justify-between mb-1.5">
          <span class="font-semibold text-slate-800 truncate">{{ name }}</span>
          <span class="text-sm font-bold ml-2 shrink-0" :class="countColor">{{ count }} <span class="text-slate-400 font-normal text-xs">{{ unit }}</span></span>
        </div>
        <!-- Progress bar -->
        <div class="h-1.5 bg-slate-100 rounded-full overflow-hidden">
          <div
            class="h-full rounded-full transition-all duration-700"
            :class="barColor"
            :style="{ width: `${Math.round((count / maxCount) * 100)}%` }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  rank: number;
  name: string;
  count: number;
  unit?: string;
  maxCount: number;
  color?: 'primary' | 'amber' | 'sky';
}>();

const medalEmoji = computed(() => ['🥇', '🥈', '🥉'][props.rank - 1] ?? '');
const medalClass = computed(() => {
  const map = ['bg-amber-50 border-2 border-amber-300', 'bg-slate-100 border-2 border-slate-300', 'bg-orange-50 border-2 border-orange-300'];
  return map[props.rank - 1] ?? '';
});

const countColor = computed(() => {
  const map = { primary: 'text-primary-600', amber: 'text-amber-600', sky: 'text-sky-600' };
  return map[props.color ?? 'primary'];
});

const barColor = computed(() => {
  if (props.rank === 1) return 'bg-gradient-to-r from-amber-400 to-amber-500';
  if (props.rank === 2) return 'bg-gradient-to-r from-slate-400 to-slate-500';
  const map = { primary: 'bg-primary-500', amber: 'bg-amber-500', sky: 'bg-sky-500' };
  return map[props.color ?? 'primary'];
});
</script>
