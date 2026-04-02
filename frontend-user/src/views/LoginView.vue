<template>
  <div class="min-h-screen flex">
    <!-- Left: Hero Panel -->
    <div class="hidden lg:flex lg:w-1/2 relative overflow-hidden bg-gradient-to-br from-primary-700 via-primary-600 to-amber-500">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-16 left-16 w-64 h-64 rounded-full bg-white/30 blur-3xl" />
        <div class="absolute bottom-24 right-12 w-80 h-80 rounded-full bg-white/20 blur-3xl" />
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 rounded-full bg-white/10 blur-3xl" />
      </div>

      <div class="relative z-10 flex flex-col justify-between p-12 text-white w-full">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-white/20 flex items-center justify-center text-xl">🏔</div>
          <span class="font-bold text-xl">山城打卡</span>
        </div>

        <div class="space-y-6">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 px-3 py-1.5 bg-white/20 backdrop-blur-sm rounded-full text-sm font-medium">
              <span>🌶</span> 重庆 · 城市探索平台
            </div>
            <h1 class="text-4xl font-bold leading-tight">
              探索山城之美<br />
              <span class="text-amber-200">记录每一刻精彩</span>
            </h1>
            <p class="text-white/80 text-lg leading-relaxed">
              打卡重庆美食美景，与更多人分享<br />
              属于你的山城故事
            </p>
          </div>

          <!-- Feature badges -->
          <div class="grid grid-cols-2 gap-3">
            <div v-for="f in features" :key="f.text" class="flex items-center gap-2.5 px-4 py-3 bg-white/15 backdrop-blur-sm rounded-xl">
              <span class="text-xl">{{ f.emoji }}</span>
              <span class="text-sm font-medium">{{ f.text }}</span>
            </div>
          </div>
        </div>

        <p class="text-white/50 text-sm">© 2026 山城打卡 · 重庆城市打卡平台</p>
      </div>
    </div>

    <!-- Right: Login Form -->
    <div class="flex-1 flex items-center justify-center p-6 sm:p-12 bg-white">
      <div class="w-full max-w-md space-y-8 animate-slide-up">
        <!-- Mobile logo -->
        <div class="flex items-center gap-2.5 lg:hidden">
          <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center text-lg shadow-sm">🏔</div>
          <span class="font-bold text-xl text-slate-800">山城打卡</span>
        </div>

        <div class="space-y-2">
          <h2 class="text-3xl font-bold text-slate-900">欢迎回来</h2>
          <p class="text-slate-500">登录你的账号，继续探索山城</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div class="space-y-1.5">
            <label class="text-sm font-medium text-slate-700">用户名</label>
            <div class="relative">
              <UserIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-slate-400 pointer-events-none" />
              <input
                v-model="form.username"
                type="text"
                placeholder="请输入用户名"
                class="input-field pl-10"
                :class="errors.username && 'border-red-400 focus:ring-red-400'"
                autocomplete="username"
                @input="errors.username = ''"
              />
            </div>
            <p v-if="errors.username" class="text-xs text-red-500">{{ errors.username }}</p>
          </div>

          <div class="space-y-1.5">
            <label class="text-sm font-medium text-slate-700">密码</label>
            <div class="relative">
              <LockIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4.5 h-4.5 text-slate-400 pointer-events-none" />
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                class="input-field pl-10 pr-11"
                :class="errors.password && 'border-red-400 focus:ring-red-400'"
                autocomplete="current-password"
                @input="errors.password = ''"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
              >
                <EyeIcon v-if="!showPassword" class="w-4.5 h-4.5" />
                <EyeOffIcon v-else class="w-4.5 h-4.5" />
              </button>
            </div>
            <p v-if="errors.password" class="text-xs text-red-500">{{ errors.password }}</p>
          </div>

          <div class="flex items-center justify-between text-sm">
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="rememberMe" class="rounded border-slate-300 text-primary-600 focus:ring-primary-500" />
              <span class="text-slate-600">记住我</span>
            </label>
            <button type="button" @click="showForgot = true" class="text-primary-600 hover:text-primary-700 font-medium transition-colors">
              忘记密码？
            </button>
          </div>

          <!-- Error alert -->
          <transition
            enter-active-class="transition ease-out duration-200"
            enter-from-class="opacity-0 -translate-y-2"
            enter-to-class="opacity-100 translate-y-0"
          >
            <div v-if="errorMsg" class="flex items-center gap-2.5 p-3.5 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
              <AlertCircleIcon class="w-4 h-4 shrink-0" />
              {{ errorMsg }}
            </div>
          </transition>

          <button type="submit" class="btn-primary w-full py-3 text-base" :disabled="loading">
            <LoaderIcon v-if="loading" class="w-4.5 h-4.5 animate-spin" />
            <span>{{ loading ? '登录中...' : '登录' }}</span>
          </button>
        </form>

        <p class="text-center text-sm text-slate-500">
          还没有账号？
          <router-link to="/register" class="font-semibold text-primary-600 hover:text-primary-700 transition-colors">
            立即注册
          </router-link>
        </p>
      </div>
    </div>
  </div>

  <!-- Forgot Password Modal -->
  <transition
    enter-active-class="transition ease-out duration-200"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="transition ease-in duration-150"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div v-if="showForgot" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showForgot = false">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 space-y-5 animate-scale-in">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-bold text-slate-800">重置密码</h3>
          <button @click="showForgot = false" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-100 transition-colors">
            <XIcon class="w-4 h-4 text-slate-500" />
          </button>
        </div>
        <p class="text-sm text-slate-500">请填写您注册时使用的用户名和手机号验证身份</p>
        <div class="space-y-4">
          <input v-model="forgotForm.username" type="text" placeholder="用户名" class="input-field" />
          <input v-model="forgotForm.phone" type="tel" placeholder="注册手机号" class="input-field" />
          <input v-model="forgotForm.newPassword" type="password" placeholder="新密码（至少6位）" class="input-field" />
        </div>
        <div v-if="forgotMsg" class="p-3 rounded-xl text-sm" :class="forgotSuccess ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'">
          {{ forgotMsg }}
        </div>
        <div class="flex gap-3">
          <button class="btn-secondary flex-1" @click="showForgot = false">取消</button>
          <button class="btn-primary flex-1" @click="handleForgot" :disabled="forgotLoading">
            {{ forgotLoading ? '提交中...' : '确认重置' }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  User as UserIcon, Lock as LockIcon, Eye as EyeIcon, EyeOff as EyeOffIcon,
  AlertCircle as AlertCircleIcon, Loader as LoaderIcon, X as XIcon,
} from 'lucide-vue-next';
import { login, forgotPassword } from '@/api/auth';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const form = reactive({ username: '', password: '' });
const errors = reactive({ username: '', password: '' });
const errorMsg = ref('');
const loading = ref(false);
const showPassword = ref(false);
const rememberMe = ref(false);

const showForgot = ref(false);
const forgotForm = reactive({ username: '', phone: '', newPassword: '' });
const forgotMsg = ref('');
const forgotSuccess = ref(false);
const forgotLoading = ref(false);

const features = [
  { emoji: '🍜', text: '美食打卡' },
  { emoji: '🏞', text: '美景打卡' },
  { emoji: '📍', text: '地图定位' },
  { emoji: '🏆', text: '热度排行' },
];

function validate() {
  let valid = true;
  if (!form.username.trim()) { errors.username = '请输入用户名'; valid = false; }
  if (!form.password) { errors.password = '请输入密码'; valid = false; }
  return valid;
}

async function handleLogin() {
  errorMsg.value = '';
  if (!validate()) return;
  loading.value = true;
  try {
    const res = await login({ username: form.username, password: form.password });
    userStore.setToken(res.data.token);
    userStore.setUserInfo(res.data.userInfo);
    await nextTick();
    const redirect = (route.query.redirect as string) || '/';
    router.replace(redirect);
  } catch (e: any) {
    errorMsg.value = e.message || '登录失败，请检查用户名或密码';
  } finally {
    loading.value = false;
  }
}

async function handleForgot() {
  forgotMsg.value = '';
  if (!forgotForm.username || !forgotForm.phone || !forgotForm.newPassword) {
    forgotMsg.value = '请填写完整信息';
    return;
  }
  if (forgotForm.newPassword.length < 6) {
    forgotMsg.value = '密码至少6位';
    return;
  }
  forgotLoading.value = true;
  try {
    await forgotPassword(forgotForm);
    forgotMsg.value = '密码重置成功，请使用新密码登录';
    forgotSuccess.value = true;
    setTimeout(() => { showForgot.value = false; forgotMsg.value = ''; }, 2000);
  } catch (e: any) {
    forgotMsg.value = e.message || '重置失败，请检查用户名或手机号';
    forgotSuccess.value = false;
  } finally {
    forgotLoading.value = false;
  }
}
</script>
