<template>
  <div class="min-h-screen flex">
    <!-- Left: Hero Panel -->
    <div class="hidden lg:flex lg:w-1/2 relative overflow-hidden bg-gradient-to-br from-slate-800 via-slate-700 to-primary-700">
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-24 left-24 w-72 h-72 rounded-full bg-primary-400 blur-3xl" />
        <div class="absolute bottom-16 right-16 w-64 h-64 rounded-full bg-amber-400 blur-3xl" />
      </div>
      <div class="relative z-10 flex flex-col justify-between p-12 text-white w-full">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-white/20 flex items-center justify-center text-xl">🏔</div>
          <span class="font-bold text-xl">山城打卡</span>
        </div>
        <div class="space-y-6">
          <h1 class="text-4xl font-bold leading-tight">
            加入山城打卡<br />
            <span class="text-amber-300">开启你的探索之旅</span>
          </h1>
          <p class="text-white/70 text-lg">注册即可享受所有打卡功能，每天最多可打卡 <span class="font-bold text-amber-300">10</span> 次</p>
          <div class="space-y-3">
            <div v-for="s in steps" :key="s.title" class="flex items-center gap-3 px-4 py-3 bg-white/10 backdrop-blur-sm rounded-xl">
              <div class="w-8 h-8 rounded-full bg-primary-500 flex items-center justify-center text-sm font-bold shrink-0">{{ s.num }}</div>
              <div>
                <div class="font-semibold text-sm">{{ s.title }}</div>
                <div class="text-white/60 text-xs">{{ s.desc }}</div>
              </div>
            </div>
          </div>
        </div>
        <p class="text-white/40 text-sm">© 2026 山城打卡</p>
      </div>
    </div>

    <!-- Right: Register Form -->
    <div class="flex-1 flex items-center justify-center p-6 sm:p-10 bg-white overflow-y-auto">
      <div class="w-full max-w-md space-y-7 py-6 animate-slide-up">
        <div class="flex items-center gap-2.5 lg:hidden">
          <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center text-lg">🏔</div>
          <span class="font-bold text-xl text-slate-800">山城打卡</span>
        </div>

        <div class="space-y-2">
          <h2 class="text-3xl font-bold text-slate-900">创建账号</h2>
          <p class="text-slate-500">填写以下信息，加入山城打卡社区</p>
        </div>

        <form @submit.prevent="handleRegister" class="space-y-4">
          <!-- Username & Password -->
          <div class="grid grid-cols-1 gap-4">
            <div class="space-y-1.5">
              <label class="text-sm font-medium text-slate-700">
                用户名 <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <UserIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input v-model="form.username" type="text" placeholder="字母、数字，4-20位" class="input-field pl-10"
                  :class="errors.username && 'border-red-400 focus:ring-red-400'" @input="errors.username = ''" />
              </div>
              <p v-if="errors.username" class="text-xs text-red-500">{{ errors.username }}</p>
            </div>

            <div class="space-y-1.5">
              <label class="text-sm font-medium text-slate-700">
                密码 <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <LockIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="至少6位" class="input-field pl-10 pr-11"
                  :class="errors.password && 'border-red-400 focus:ring-red-400'" @input="errors.password = ''" />
                <button type="button" @click="showPwd = !showPwd" class="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                  <EyeIcon v-if="!showPwd" class="w-4 h-4" />
                  <EyeOffIcon v-else class="w-4 h-4" />
                </button>
              </div>
              <p v-if="errors.password" class="text-xs text-red-500">{{ errors.password }}</p>
            </div>
          </div>

          <!-- Divider -->
          <div class="flex items-center gap-3 py-1">
            <div class="flex-1 border-t border-slate-100" />
            <span class="text-xs text-slate-400">选填信息（方便找回密码）</span>
            <div class="flex-1 border-t border-slate-100" />
          </div>

          <!-- Optional fields -->
          <div class="space-y-4">
            <div class="space-y-1.5">
              <label class="text-sm font-medium text-slate-700">真实姓名</label>
              <input v-model="form.realName" type="text" placeholder="请输入真实姓名" class="input-field" />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div class="space-y-1.5">
                <label class="text-sm font-medium text-slate-700">手机号</label>
                <div class="relative">
                  <PhoneIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                  <input v-model="form.phone" type="tel" placeholder="11位手机号" class="input-field pl-10"
                    :class="errors.phone && 'border-red-400 focus:ring-red-400'" @input="errors.phone = ''" />
                </div>
                <p v-if="errors.phone" class="text-xs text-red-500">{{ errors.phone }}</p>
              </div>
              <div class="space-y-1.5">
                <label class="text-sm font-medium text-slate-700">邮箱</label>
                <div class="relative">
                  <MailIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                  <input v-model="form.email" type="email" placeholder="邮箱地址" class="input-field pl-10"
                    :class="errors.email && 'border-red-400 focus:ring-red-400'" @input="errors.email = ''" />
                </div>
                <p v-if="errors.email" class="text-xs text-red-500">{{ errors.email }}</p>
              </div>
            </div>
          </div>

          <!-- Alert -->
          <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 -translate-y-2" enter-to-class="opacity-100 translate-y-0">
            <div v-if="errorMsg" class="flex items-center gap-2.5 p-3.5 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
              <AlertCircleIcon class="w-4 h-4 shrink-0" />
              {{ errorMsg }}
            </div>
          </transition>

          <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 -translate-y-2" enter-to-class="opacity-100 translate-y-0">
            <div v-if="successMsg" class="flex items-center gap-2.5 p-3.5 bg-green-50 border border-green-200 rounded-xl text-sm text-green-700">
              <CheckCircleIcon class="w-4 h-4 shrink-0" />
              {{ successMsg }}
            </div>
          </transition>

          <button type="submit" class="btn-primary w-full py-3 text-base" :disabled="loading">
            <LoaderIcon v-if="loading" class="w-4 h-4 animate-spin" />
            <span>{{ loading ? '注册中...' : '创建账号' }}</span>
          </button>
        </form>

        <p class="text-center text-sm text-slate-500">
          已有账号？
          <router-link to="/login" class="font-semibold text-primary-600 hover:text-primary-700 transition-colors">去登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import {
  User as UserIcon, Lock as LockIcon, Eye as EyeIcon, EyeOff as EyeOffIcon,
  Phone as PhoneIcon, Mail as MailIcon, AlertCircle as AlertCircleIcon,
  CheckCircle as CheckCircleIcon, Loader as LoaderIcon,
} from 'lucide-vue-next';
import { register } from '@/api/auth';

const router = useRouter();
const showPwd = ref(false);
const loading = ref(false);
const errorMsg = ref('');
const successMsg = ref('');

const form = reactive({
  username: '', password: '', realName: '', email: '', phone: '',
});
const errors = reactive({ username: '', password: '', phone: '', email: '' });

const steps = [
  { num: '1', title: '填写基本信息', desc: '用户名和密码是必填项' },
  { num: '2', title: '完善个人资料', desc: '手机号可用于找回密码' },
  { num: '3', title: '开始探索打卡', desc: '每天最多打卡10次' },
];

function validate() {
  let valid = true;
  if (!form.username.trim() || form.username.length < 4) {
    errors.username = '用户名至少4位'; valid = false;
  }
  if (form.password.length < 6) {
    errors.password = '密码至少6位'; valid = false;
  }
  if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
    errors.phone = '手机号格式不正确'; valid = false;
  }
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = '邮箱格式不正确'; valid = false;
  }
  return valid;
}

async function handleRegister() {
  errorMsg.value = '';
  successMsg.value = '';
  if (!validate()) return;
  loading.value = true;
  try {
    await register({
      username: form.username,
      password: form.password,
      realName: form.realName || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
    });
    successMsg.value = '注册成功！即将跳转到登录页...';
    setTimeout(() => router.push('/login'), 1500);
  } catch (e: any) {
    errorMsg.value = e.message || '注册失败，请稍后再试';
  } finally {
    loading.value = false;
  }
}
</script>
