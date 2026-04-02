<template>
  <header
    class="sticky top-0 z-50 w-full border-b border-white/30"
    :class="scrolled ? 'bg-white/70 backdrop-blur-xl shadow-sm' : 'bg-transparent'"
  >
    <div class="container-page">
      <div class="flex items-center justify-between h-16">
        <router-link to="/" class="flex items-center gap-2.5 group">
          <div
            class="w-9 h-9 rounded-xl bg-gradient-to-br from-cyan-500 to-cyan-700 flex items-center justify-center shadow-sm group-hover:shadow transition-shadow"
            aria-hidden="true"
          >
            <MountainIcon class="w-5 h-5 text-white" />
          </div>
          <div class="leading-none">
            <div class="font-semibold text-slate-900">CityCheckin</div>
            <div class="text-xs text-slate-500">重庆城市打卡</div>
          </div>
        </router-link>

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

        <div class="flex items-center gap-2.5">
          <router-link
            v-if="userStore.isLoggedIn"
            to="/checkin/new"
            class="md:hidden btn-primary px-3.5 py-2"
          >
            <PlusIcon class="w-4 h-4" />
            打卡
          </router-link>

          <div v-if="userStore.isLoggedIn" class="relative" ref="dropdownRef">
            <button
              class="flex items-center gap-2 px-2.5 py-2 rounded-xl hover:bg-white/60 transition-colors"
              @click="showDropdown = !showDropdown"
            >
              <div
                class="w-8 h-8 rounded-xl bg-gradient-to-br from-cyan-500 to-cyan-700 text-white flex items-center justify-center font-semibold text-sm shadow-sm"
              >
                {{ userStore.displayName.charAt(0).toUpperCase() }}
              </div>
              <span class="hidden sm:block text-sm font-medium text-slate-700">
                {{ userStore.displayName }}
              </span>
              <ChevronDownIcon class="w-4 h-4 text-slate-400 transition-transform" :class="showDropdown && 'rotate-180'" />
            </button>

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
                class="absolute right-0 mt-2 w-52 rounded-2xl bg-white/80 backdrop-blur-xl border border-white/40 shadow-lg overflow-hidden"
              >
                <router-link
                  to="/profile"
                  class="dropdown-item"
                  @click="showDropdown = false"
                >
                  <UserIcon class="w-4 h-4 text-slate-500" />
                  个人中心
                </router-link>
                <router-link
                  to="/my-checkins"
                  class="dropdown-item md:hidden"
                  @click="showDropdown = false"
                >
                  <ClipboardListIcon class="w-4 h-4 text-slate-500" />
                  我的打卡
                </router-link>
                <div class="h-px bg-white/40" />
                <button class="dropdown-item text-red-600" @click="handleLogout">
                  <LogOutIcon class="w-4 h-4" />
                  退出登录
                </button>
              </div>
            </transition>
          </div>

          <template v-else>
            <router-link to="/login" class="btn-secondary">登录</router-link>
            <router-link to="/register" class="btn-primary">注册</router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import {
  ChevronDown as ChevronDownIcon,
  ClipboardList as ClipboardListIcon,
  Home as HomeIcon,
  LogOut as LogOutIcon,
  Mountain as MountainIcon,
  Plus as PlusIcon,
  PlusCircle as PlusCircleIcon,
  User as UserIcon,
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
  @apply inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-sm font-medium text-slate-600 hover:text-cyan-700 hover:bg-white/60 transition-colors;
}
.nav-link-active {
  @apply text-cyan-700 bg-white/60;
}
.dropdown-item {
  @apply w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-slate-700 hover:bg-white/60 transition-colors;
}
</style>

