<template>
  <el-button
      v-permission="permission"
      :type="type"
      :icon="getOpButtonIcon()"
      :label="label"
      :disabled="disabled"
      :loading="loading"
      @click="btnSubmit"
  >
    {{ getOpButtonText() }}
  </el-button>
</template>

<script lang="ts">
  import { useAuthStore } from '@/store/auth';

  export default {
    name: 'permission-btn',
    props: {
      label: String,
      //按钮类型
      type: String,
      //权限指令字段
      permission: String,
      //是否禁用
      disabled: Boolean,
      loading: Boolean
    },
    setup(props: any, { emit }: any) {
      const authStore = useAuthStore();

      const methods = {
        btnSubmit() {
          emit("btnSubmit");
        },
      };

      /**
       * 获取按钮的文本内容
       */
      const getOpButtonText = () => {
        const obj: any = authStore.authButtonList.find((item: any) => item.code === props.permission);
        return obj?.name ?? '';
      };

      /**
       * 获取按钮的icon
       */
      const getOpButtonIcon = () => {
        const obj: any = authStore.authButtonList.find((item: any) => item.code === props.permission);
        return obj?.icon ?? '';
      };

      return {
        ...methods,
        getOpButtonText,
        getOpButtonIcon
      };
    },
  };
</script>

<style>
</style>
