<template>
  <div class="min-h-screen bg-slate-50">
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
