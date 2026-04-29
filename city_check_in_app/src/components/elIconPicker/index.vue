<template>
  <el-popover trigger="focus" :width="350">
    <template #reference>
      <div v-if="isIconNameValid(modelValue)">
        <el-button :icon="modelValue">{{ modelValue }}</el-button>
        <el-button type="info" :icon="Delete" circle @click="emits('update:modelValue', '')"/>
      </div>
      <template v-else>
        <el-button>请选择icon</el-button>
      </template>
    </template>
    <div class="el-icon-picker">
      <component v-for="icon in globalProperties.$icons" :key="icon"
                 :class="[icon, 'icon', { 'icon-active': icon === modelValue }]"
                 :is="icon"
                 @click="emits('update:modelValue', icon)">
      </component>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
  import { defineEmits, defineProps, getCurrentInstance } from 'vue';
  import type { ComponentInternalInstance } from 'vue';
  import { Delete } from '@element-plus/icons-vue';

  const { appContext: { app: { config: { globalProperties } } } } = getCurrentInstance() as ComponentInternalInstance;

  interface Props {
    modelValue: string
  }

  const props = defineProps<Props>();

  const emits = defineEmits(['update:modelValue']);

  // 用于验证图标名称是否有效
  const isIconNameValid = (name: string) => {
    // ElementPlusIconsObject是Element Plus图标库对象，你可以通过查看它来查看所有有效的图标名称
    const ElementPlusIconsObject = globalProperties.$icons ?? [];
    return ElementPlusIconsObject.includes(name);
  };

</script>


<style scoped>
  .el-icon-picker {
    height: 256px;
    overflow-y: scroll;
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
  }

  .icon {
    display: inline-block;
    width: 24px;
    height: 24px;
    color: var(--el-text-color-regular);
    font-size: 20px;
    border-radius: 4px;
    cursor: pointer;
    text-align: center;
    line-height: 45px;
    margin: 5px;
  }

  .icon:hover {
    color: var(--el-color-primary);
  }

  .icon-active {
    color: var(--el-color-primary);
  }
</style>
