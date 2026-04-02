<template>
  <div class="min-h-screen">
    <Navbar />
    <router-view v-slot="{ Component, route }">
      <transition name="page">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
</script>

<style>
.page-enter-active,
.page-leave-active {
  transition: opacity 150ms ease, transform 150ms ease;
}
.page-enter-from,
.page-leave-to {
  opacity: 0;
  transform: translateY(6px);
}
</style>
