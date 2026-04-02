<template>
  <header
    class="sticky top-0 z-50 w-full"
    :class="scrolled ? 'bg-white/95 backdrop-blur-md shadow-sm border-b border-slate-100' : 'bg-transparent'"
  >
    <div class="max-w-6xl mx-auto px-4 sm:px-6">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <router-link to="/" class="flex items-center gap-2.5 group">
          <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center shadow-sm group-hover:scale-105 transition-transform">
            <span class="text-white text-lg">🏔</span>
          </div>
          <div class="flex flex-col leading-none">
            <span class="font-bold text-slate-800 text-base">山城打卡</span>
            <span class="text-xs text-slate-400 font-normal">探索重庆之美</span>
          </div>
        </router-link>

        <!-- Desktop Nav -->
        <nav class="hidden md:flex items-center gap-1">
          <router-link to="/" class="nav-link" active-class="nav-link-active">
            <HomeIcon class="w-4 h-4" />
            首页
          </router-link>
          <template v-if="userStore.isLoggedIn">
            <router-link to="/checkin/new" class="nav-link" active-class="nav-link-active">
              <PlusCircleIcon class="w-4 h-4" />
              发起打卡
            </router-link>
            <router-link to="/my-checkins" class="nav-link" active-class="nav-link-active">
              <ClipboardListIcon class="w-4 h-4" />
              我的打卡
            </router-link>
          </template>
        </nav>

        <!-- Right side -->
        <div class="flex items-center gap-3">
          <!-- 发起打卡按钮 (mobile) -->
          <template v-if="userStore.isLoggedIn">
            <router-link
              to="/checkin/new"
              class="md:hidden flex items-center gap-1.5 px-3.5 py-2 bg-primary-600 hover:bg-primary-700 text-white text-sm font-medium rounded-xl shadow-sm transition-all"
            >
              <PlusIcon class="w-4 h-4" />
              打卡
            </router-link>
          </template>

          <!-- User dropdown -->
          <div v-if="userStore.isLoggedIn" class="relative" ref="dropdownRef">
            <button
              @click="showDropdown = !showDropdown"
              class="flex items-center gap-2 px-3 py-2 rounded-xl hover:bg-slate-100 transition-colors"
            >
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center text-white font-semibold text-sm shadow-sm">
                {{ userStore.displayName.charAt(0).toUpperCase() }}
              </div>
              <span class="hidden sm:block text-sm font-medium text-slate-700">{{ userStore.displayName }}</span>
              <ChevronDownIcon class="w-4 h-4 text-slate-400 transition-transform" :class="showDropdown && 'rotate-180'" />
            </button>

            <!-- Dropdown -->
            <transition
              enter-active-class="transition ease-out duration-150"
              enter-from-class="opacity-0 scale-95 translate-y-1"
              enter-to-class="opacity-100 scale-100 translate-y-0"
              leave-active-class="transition ease-in duration-100"
              leave-from-class="opacity-100 scale-100 translate-y-0"
              leave-to-class="opacity-0 scale-95 translate-y-1"
            >
              <div
                v-if="showDropdown"
                class="absolute right-0 mt-2 w-48 bg-white rounded-2xl shadow-lg border border-slate-100 py-1 overflow-hidden"
              >
                <router-link
                  to="/profile"
                  class="flex items-center gap-2.5 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                  @click="showDropdown = false"
                >
                  <UserIcon class="w-4 h-4 text-slate-400" />
                  个人中心
                </router-link>
                <router-link
                  to="/my-checkins"
                  class="flex items-center gap-2.5 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors md:hidden"
                  @click="showDropdown = false"
                >
                  <ClipboardListIcon class="w-4 h-4 text-slate-400" />
                  我的打卡
                </router-link>
                <div class="border-t border-slate-100 my-1" />
                <button
                  @click="handleLogout"
                  class="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition-colors"
                >
                  <LogOutIcon class="w-4 h-4" />
                  退出登录
                </button>
              </div>
            </transition>
          </div>

          <!-- Login / Register -->
          <template v-else>
            <router-link to="/login" class="btn-secondary text-sm px-4 py-2">登录</router-link>
            <router-link to="/register" class="btn-primary text-sm px-4 py-2">注册</router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import {
  Home as HomeIcon,
  PlusCircle as PlusCircleIcon,
  Plus as PlusIcon,
  ClipboardList as ClipboardListIcon,
  User as UserIcon,
  ChevronDown as ChevronDownIcon,
  LogOut as LogOutIcon,
} from 'lucide-vue-next';

const userStore = useUserStore();
const router = useRouter();

const scrolled = ref(false);
const showDropdown = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

function handleScroll() {
  scrolled.value = window.scrollY > 10;
}

function handleClickOutside(e: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    showDropdown.value = false;
  }
}

async function handleLogout() {
  showDropdown.value = false;
  await userStore.logout();
  router.push('/');
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true });
  document.addEventListener('click', handleClickOutside);
  handleScroll();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.nav-link {
  @apply flex items-center gap-1.5 px-3.5 py-2 text-sm font-medium text-slate-600 hover:text-primary-600 hover:bg-primary-50 rounded-xl transition-all duration-200;
}
.nav-link-active {
  @apply text-primary-600 bg-primary-50 !important;
}
</style>
