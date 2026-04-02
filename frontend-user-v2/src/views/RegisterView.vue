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
              创建账号
            </h1>
            <p class="mt-2 text-slate-600">
              注册后即可发起打卡与查看个人记录（每天最多 10 次）。
            </p>

            <div class="mt-7 space-y-3 text-sm">
              <div class="rounded-2xl bg-white/60 border border-white/50 p-4 flex items-center gap-3">
                <div class="w-9 h-9 rounded-2xl bg-cyan-600/10 text-cyan-700 flex items-center justify-center font-semibold">1</div>
                <div>
                  <div class="font-medium text-slate-800">设置用户名与密码</div>
                  <div class="text-slate-600 mt-0.5">用户名建议 4-20 位</div>
                </div>
              </div>
              <div class="rounded-2xl bg-white/60 border border-white/50 p-4 flex items-center gap-3">
                <div class="w-9 h-9 rounded-2xl bg-cyan-600/10 text-cyan-700 flex items-center justify-center font-semibold">2</div>
                <div>
                  <div class="font-medium text-slate-800">完善联系方式（可选）</div>
                  <div class="text-slate-600 mt-0.5">便于找回密码</div>
                </div>
              </div>
              <div class="rounded-2xl bg-white/60 border border-white/50 p-4 flex items-center gap-3">
                <div class="w-9 h-9 rounded-2xl bg-cyan-600/10 text-cyan-700 flex items-center justify-center font-semibold">3</div>
                <div>
                  <div class="font-medium text-slate-800">开始打卡</div>
                  <div class="text-slate-600 mt-0.5">发布后进入审核流程</div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="lg:col-span-7 card p-6 sm:p-8">
          <div class="max-w-md">
            <h2 class="text-xl font-semibold text-slate-900">注册账号</h2>
            <p class="mt-1 text-sm text-slate-600">带 * 的为必填，其余为选填信息。</p>

            <form class="mt-6 space-y-4" @submit.prevent="handleRegister">
              <div>
                <label class="text-sm font-medium text-slate-700">用户名 *</label>
                <div class="mt-1.5 relative">
                  <UserIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                  <input
                    v-model="form.username"
                    class="input pl-10"
                    placeholder="字母、数字，4-20位"
                    @input="errors.username = ''"
                  />
                </div>
                <p v-if="errors.username" class="mt-1 text-xs text-red-600">{{ errors.username }}</p>
              </div>

              <div>
                <label class="text-sm font-medium text-slate-700">密码 *</label>
                <div class="mt-1.5 relative">
                  <LockIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                  <input
                    v-model="form.password"
                    :type="showPassword ? 'text' : 'password'"
                    class="input pl-10 pr-10"
                    placeholder="至少6位"
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

              <div class="h-px bg-white/60" />
              <div class="text-xs text-slate-500">选填信息（用于找回密码/联系）</div>

              <div>
                <label class="text-sm font-medium text-slate-700">真实姓名</label>
                <input v-model="form.realName" class="input mt-1.5" placeholder="请输入真实姓名" />
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="text-sm font-medium text-slate-700">手机号</label>
                  <div class="mt-1.5 relative">
                    <PhoneIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                    <input
                      v-model="form.phone"
                      class="input pl-10"
                      placeholder="11位手机号"
                      @input="errors.phone = ''"
                    />
                  </div>
                  <p v-if="errors.phone" class="mt-1 text-xs text-red-600">{{ errors.phone }}</p>
                </div>
                <div>
                  <label class="text-sm font-medium text-slate-700">邮箱</label>
                  <div class="mt-1.5 relative">
                    <MailIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                    <input
                      v-model="form.email"
                      class="input pl-10"
                      placeholder="邮箱地址"
                      @input="errors.email = ''"
                    />
                  </div>
                  <p v-if="errors.email" class="mt-1 text-xs text-red-600">{{ errors.email }}</p>
                </div>
              </div>

              <div v-if="errorMsg" class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 flex items-center gap-2">
                <AlertCircleIcon class="w-4 h-4" />
                {{ errorMsg }}
              </div>
              <div v-if="successMsg" class="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800 flex items-center gap-2">
                <CheckCircleIcon class="w-4 h-4" />
                {{ successMsg }}
              </div>

              <button class="btn-primary w-full py-2.5" :disabled="loading">
                <LoaderIcon v-if="loading" class="w-4 h-4 animate-spin" />
                <span>{{ loading ? '注册中...' : '创建账号' }}</span>
              </button>
            </form>

            <div class="mt-4 text-sm text-slate-600">
              已有账号？
              <router-link to="/login" class="text-cyan-700 font-semibold hover:text-cyan-800">去登录</router-link>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '@/api/auth';
import {
  AlertCircle as AlertCircleIcon,
  CheckCircle as CheckCircleIcon,
  Eye as EyeIcon,
  EyeOff as EyeOffIcon,
  Loader as LoaderIcon,
  Lock as LockIcon,
  Mail as MailIcon,
  Mountain as MountainIcon,
  Phone as PhoneIcon,
  User as UserIcon,
} from 'lucide-vue-next';

const router = useRouter();

const showPassword = ref(false);
const loading = ref(false);
const errorMsg = ref('');
const successMsg = ref('');

const form = reactive({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
});

const errors = reactive({
  username: '',
  password: '',
  phone: '',
  email: '',
});

function validate() {
  let valid = true;
  if (!form.username.trim() || form.username.trim().length < 4) {
    errors.username = '用户名至少4位';
    valid = false;
  }
  if (form.password.length < 6) {
    errors.password = '密码至少6位';
    valid = false;
  }
  if (form.phone && !/^1[3-9]\\d{9}$/.test(form.phone)) {
    errors.phone = '手机号格式不正确';
    valid = false;
  }
  if (form.email && !/^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/.test(form.email)) {
    errors.email = '邮箱格式不正确';
    valid = false;
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
    setTimeout(() => router.push('/login'), 1200);
  } catch (e: any) {
    errorMsg.value = e?.message || '注册失败，请稍后再试';
  } finally {
    loading.value = false;
  }
}
</script>

