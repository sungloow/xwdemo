<template>
  <div class="card p-5 hover:shadow-md hover:-translate-y-0.5 transition-all duration-200">
    <div class="flex flex-col gap-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div
            class="w-9 h-9 rounded-xl flex items-center justify-center text-base font-bold shrink-0"
            :class="rank <= 3 ? 'bg-amber-50 text-amber-600' : 'bg-slate-100 text-slate-500'"
          >
            {{ rank <= 3 ? ['🥇','🥈','🥉'][rank-1] : rank }}
          </div>
          <div>
            <div class="font-semibold text-slate-800">{{ name }}</div>
            <div class="text-xs text-amber-600 font-medium">{{ count }} 次打卡</div>
          </div>
        </div>
      </div>
      <div class="space-y-1">
        <div class="flex justify-between text-xs text-slate-400">
          <span>热度</span>
          <span>{{ Math.round((count / maxCount) * 100) }}%</span>
        </div>
        <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
          <div
            class="h-full bg-gradient-to-r from-amber-400 to-orange-500 rounded-full transition-all duration-700"
            :style="{ width: `${Math.round((count / maxCount) * 100)}%` }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  rank: number;
  name: string;
  count: number;
  maxCount: number;
}>();
</script>
