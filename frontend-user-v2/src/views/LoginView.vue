<template>
  <main class="min-h-[calc(100vh-4rem)]">
    <div class="container-page py-10 sm:py-14">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-stretch">
        <section class="lg:col-span-5 card p-6 sm:p-8 overflow-hidden relative">
          <div class="absolute -top-24 -right-24 w-80 h-80 rounded-full bg-cyan-400/25 blur-3xl" />
          <div class="absolute -bottom-28 -left-28 w-80 h-80 rounded-full bg-emerald-400/15 blur-3xl" />

          <div class="relative">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-cyan-600 text-white flex items-center justify-center shadow-sm">
                <MountainIcon class="w-5 h-5" />
              </div>
              <div>
                <div class="font-semibold text-slate-900">CityCheckin</div>
                <div class="text-xs text-slate-500">重庆城市打卡</div>
              </div>
            </div>

            <h1 class="mt-8 text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
              欢迎回来
            </h1>
            <p class="mt-2 text-slate-600">
              登录后可以发起打卡、查看个人记录与编辑资料。
            </p>

            <div class="mt-7 grid grid-cols-2 gap-3 text-sm">
              <div class="rounded-2xl bg-white/60 border border-white/50 p-4">
                <div class="font-medium text-slate-800">更清爽的 UI</div>
                <div class="text-slate-600 mt-1">玻璃拟态 + 轻量阴影</div>
              </div>
              <div class="rounded-2xl bg-white/60 border border-white/50 p-4">
                <div class="font-medium text-slate-800">更稳定交互</div>
                <div class="text-slate-600 mt-1">更好的表单与反馈</div>
              </div>
            </div>
          </div>
        </section>

        <section class="lg:col-span-7 card p-6 sm:p-8">
          <div class="max-w-md">
            <div class="flex items-center gap-2 text-sm text-slate-500">
              <ShieldIcon class="w-4 h-4" />
              使用后端统一鉴权（Bearer Token）
            </div>

            <h2 class="mt-3 text-xl font-semibold text-slate-900">登录账号</h2>
            <p class="mt-1 text-sm text-slate-600">请输入用户名与密码。</p>

            <form class="mt-6 space-y-4" @submit.prevent="handleLogin">
              <div>
                <label class="text-sm font-medium text-slate-700">用户名</label>
                <div class="mt-1.5 relative">
                  <UserIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                  <input
                    v-model="form.username"
                    class="input pl-10"
                    autocomplete="username"
                    placeholder="请输入用户名"
                    @input="errors.username = ''"
                  />
                </div>
                <p v-if="errors.username" class="mt-1 text-xs text-red-600">{{ errors.username }}</p>
              </div>

              <div>
                <label class="text-sm font-medium text-slate-700">密码</label>
                <div class="mt-1.5 relative">
                  <LockIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                  <input
                    v-model="form.password"
                    :type="showPassword ? 'text' : 'password'"
                    class="input pl-10 pr-10"
                    autocomplete="current-password"
                    placeholder="请输入密码"
                    @input="errors.password = ''"
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                    @click="showPassword = !showPassword"
                  >
                    <EyeIcon v-if="!showPassword" class="w-4 h-4" />
                    <EyeOffIcon v-else class="w-4 h-4" />
                  </button>
                </div>
                <p v-if="errors.password" class="mt-1 text-xs text-red-600">{{ errors.password }}</p>
              </div>

              <div v-if="errorMsg" class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 flex items-center gap-2">
                <AlertCircleIcon class="w-4 h-4" />
                {{ errorMsg }}
              </div>

              <button class="btn-primary w-full py-2.5" :disabled="loading">
                <LoaderIcon v-if="loading" class="w-4 h-4 animate-spin" />
                <span>{{ loading ? '登录中...' : '登录' }}</span>
              </button>
            </form>

            <div class="mt-4 text-sm text-slate-600">
              还没有账号？
              <router-link to="/register" class="text-cyan-700 font-semibold hover:text-cyan-800">去注册</router-link>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { login } from '@/api/auth';
import { useUserStore } from '@/stores/user';
import {
  AlertCircle as AlertCircleIcon,
  Eye as EyeIcon,
  EyeOff as EyeOffIcon,
  Loader as LoaderIcon,
  Lock as LockIcon,
  Mountain as MountainIcon,
  Shield as ShieldIcon,
  User as UserIcon,
} from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const form = reactive({ username: '', password: '' });
const errors = reactive({ username: '', password: '' });
const errorMsg = ref('');
const loading = ref(false);
const showPassword = ref(false);

function validate() {
  let valid = true;
  if (!form.username.trim()) {
    errors.username = '请输入用户名';
    valid = false;
  }
  if (!form.password) {
    errors.password = '请输入密码';
    valid = false;
  }
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
    errorMsg.value = e?.message || '登录失败，请检查用户名或密码';
  } finally {
    loading.value = false;
  }
}
</script>

