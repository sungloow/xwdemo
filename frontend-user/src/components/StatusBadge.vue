<template>
  <span class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium" :class="config.class">
    <component :is="config.icon" class="w-3 h-3" />
    {{ config.text }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Clock, CheckCircle, XCircle } from 'lucide-vue-next';

const props = defineProps<{ status: 0 | 1 | 2 }>();

const config = computed(() => {
  const map = {
    0: { text: '审核中', class: 'bg-amber-100 text-amber-700', icon: Clock },
    1: { text: '已发布', class: 'bg-green-100 text-green-700', icon: CheckCircle },
    2: { text: '未通过', class: 'bg-red-100 text-red-700', icon: XCircle },
  };
  return map[props.status];
});
</script>
