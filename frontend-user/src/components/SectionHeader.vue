<template>
  <div class="flex items-start justify-between">
    <div class="space-y-1">
      <div class="flex items-center gap-2">
        <span
          class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-semibold"
          :class="tagClass"
        >{{ tag?.text }}</span>
      </div>
      <h2 class="text-2xl font-bold text-slate-800 flex items-center gap-2.5">
        <span class="text-2xl">{{ emoji }}</span>
        {{ title }}
      </h2>
      <p class="text-sm text-slate-500">{{ subtitle }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  emoji: string;
  title: string;
  subtitle: string;
  tag?: { text: string; color: 'primary' | 'amber' | 'sky' | 'green' };
}>();

const tagClass = computed(() => {
  const map = {
    primary: 'bg-primary-100 text-primary-700',
    amber: 'bg-amber-100 text-amber-700',
    sky: 'bg-sky-100 text-sky-700',
    green: 'bg-green-100 text-green-700',
  };
  return map[props.tag?.color ?? 'primary'];
});
</script>
