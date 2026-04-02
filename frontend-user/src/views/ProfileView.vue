<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 py-10 space-y-8">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-2">
      <router-link to="/" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-100 transition-colors">
        <ArrowLeftIcon class="w-4 h-4 text-slate-600" />
      </router-link>
      <h1 class="text-2xl font-bold text-slate-900">个人中心</h1>
    </div>

    <!-- Profile Card -->
    <div class="card overflow-visible">
      <!-- Cover -->
      <div class="h-28 bg-gradient-to-r from-primary-600 via-primary-500 to-amber-400 rounded-t-2xl relative">
        <div class="absolute inset-0 opacity-20" style="background-image: radial-gradient(circle, white 1px, transparent 1px); background-size: 20px 20px;" />
      </div>
      <!-- Avatar + Info -->
      <div class="px-6 pb-6">
        <div class="flex items-end justify-between -mt-10 mb-5">
          <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-primary-400 to-primary-700 border-4 border-white shadow-lg flex items-center justify-center text-3xl font-bold text-white">
            {{ userStore.displayName.charAt(0).toUpperCase() }}
          </div>
          <button
            @click="isEditing = !isEditing"
            class="flex items-center gap-1.5 px-4 py-2 text-sm font-medium rounded-xl border transition-all"
            :class="isEditing ? 'border-red-200 text-red-600 hover:bg-red-50' : 'border-slate-200 text-slate-600 hover:bg-slate-50'"
          >
            <component :is="isEditing ? XIcon : EditIcon" class="w-4 h-4" />
            {{ isEditing ? '取消' : '编辑资料' }}
          </button>
        </div>

        <!-- Display mode -->
        <div v-if="!isEditing" class="space-y-1">
          <h2 class="text-xl font-bold text-slate-900">{{ userStore.displayName }}</h2>
          <p class="text-slate-500 text-sm">@{{ userStore.userInfo?.username }}</p>
          <div class="flex flex-wrap gap-4 mt-4 text-sm">
            <div v-if="userStore.userInfo?.email" class="flex items-center gap-1.5 text-slate-600">
              <MailIcon class="w-4 h-4 text-slate-400" />
              {{ userStore.userInfo.email }}
            </div>
            <div v-if="userStore.userInfo?.phone" class="flex items-center gap-1.5 text-slate-600">
              <PhoneIcon class="w-4 h-4 text-slate-400" />
              {{ userStore.userInfo.phone }}
            </div>
          </div>
        </div>

        <!-- Edit mode -->
        <form v-else @submit.prevent="handleSave" class="space-y-4 mt-2">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-sm font-medium text-slate-700">真实姓名</label>
              <input v-model="editForm.realName" type="text" placeholder="请输入真实姓名" class="input-field" />
            </div>
            <div class="space-y-1.5">
              <label class="text-sm font-medium text-slate-700">手机号</label>
              <div class="relative">
                <PhoneIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input v-model="editForm.phone" type="tel" placeholder="11位手机号" class="input-field pl-10"
                  :class="editErrors.phone && 'border-red-400 focus:ring-red-400'" @input="editErrors.phone = ''" />
              </div>
              <p v-if="editErrors.phone" class="text-xs text-red-500">{{ editErrors.phone }}</p>
            </div>
            <div class="space-y-1.5 sm:col-span-2">
              <label class="text-sm font-medium text-slate-700">邮箱</label>
              <div class="relative">
                <MailIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input v-model="editForm.email" type="email" placeholder="邮箱地址" class="input-field pl-10"
                  :class="editErrors.email && 'border-red-400 focus:ring-red-400'" @input="editErrors.email = ''" />
              </div>
              <p v-if="editErrors.email" class="text-xs text-red-500">{{ editErrors.email }}</p>
            </div>
          </div>

          <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 -translate-y-2" enter-to-class="opacity-100 translate-y-0">
            <div v-if="saveMsg" class="flex items-center gap-2 p-3 rounded-xl text-sm"
              :class="saveSuccess ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'">
              <component :is="saveSuccess ? CheckCircleIcon : AlertCircleIcon" class="w-4 h-4 shrink-0" />
              {{ saveMsg }}
            </div>
          </transition>

          <div class="flex gap-3 pt-1">
            <button type="button" @click="isEditing = false" class="btn-secondary flex-1">取消</button>
            <button type="submit" class="btn-primary flex-1" :disabled="saving">
              <LoaderIcon v-if="saving" class="w-4 h-4 animate-spin" />
              {{ saving ? '保存中...' : '保存修改' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-3 gap-4">
      <div v-for="s in checkinStats" :key="s.label" class="card p-5 text-center hover:shadow-md transition-shadow">
        <div class="text-3xl font-bold" :class="s.color">{{ s.value }}</div>
        <div class="text-sm text-slate-500 mt-1">{{ s.label }}</div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="card p-6">
      <h3 class="font-semibold text-slate-700 mb-4">快捷操作</h3>
      <div class="grid grid-cols-2 gap-3">
        <router-link
          v-for="action in quickActions"
          :key="action.label"
          :to="action.to"
          class="flex items-center gap-3 p-4 rounded-xl border border-slate-100 hover:border-primary-200 hover:bg-primary-50 transition-all group"
        >
          <div class="w-10 h-10 rounded-xl flex items-center justify-center text-xl shrink-0" :class="action.bg">
            {{ action.emoji }}
          </div>
          <div>
            <div class="font-medium text-sm text-slate-700 group-hover:text-primary-700">{{ action.label }}</div>
            <div class="text-xs text-slate-400">{{ action.desc }}</div>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Danger Zone -->
    <div class="card p-6 border-red-100">
      <h3 class="font-semibold text-red-600 mb-1">退出登录</h3>
      <p class="text-sm text-slate-400 mb-4">退出后需要重新登录才能使用打卡功能</p>
      <button @click="handleLogout" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-red-600 border border-red-200 rounded-xl hover:bg-red-50 transition-colors">
        <LogOutIcon class="w-4 h-4" />
        退出登录
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  ArrowLeft as ArrowLeftIcon, Edit as EditIcon, X as XIcon, Mail as MailIcon,
  Phone as PhoneIcon, CheckCircle as CheckCircleIcon, AlertCircle as AlertCircleIcon,
  Loader as LoaderIcon, LogOut as LogOutIcon,
} from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { updateUser } from '@/api/user';
import { getMyCheckinList } from '@/api/checkin';

const userStore = useUserStore();
const router = useRouter();

const isEditing = ref(false);
const saving = ref(false);
const saveMsg = ref('');
const saveSuccess = ref(false);

const editForm = reactive({
  realName: userStore.userInfo?.realName || '',
  email: userStore.userInfo?.email || '',
  phone: userStore.userInfo?.phone || '',
});
const editErrors = reactive({ phone: '', email: '' });

const checkinStats = reactive([
  { label: '全部打卡', value: 0, color: 'text-slate-700' },
  { label: '已发布', value: 0, color: 'text-green-600' },
  { label: '待审核', value: 0, color: 'text-amber-600' },
]);

const quickActions = [
  { to: '/checkin/new', emoji: '📍', label: '发起打卡', desc: '记录新的精彩', bg: 'bg-primary-50' },
  { to: '/my-checkins', emoji: '📋', label: '我的打卡', desc: '查看历史记录', bg: 'bg-sky-50' },
];

function validateEdit() {
  let valid = true;
  if (editForm.phone && !/^1[3-9]\d{9}$/.test(editForm.phone)) {
    editErrors.phone = '手机号格式不正确'; valid = false;
  }
  if (editForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.email)) {
    editErrors.email = '邮箱格式不正确'; valid = false;
  }
  return valid;
}

async function handleSave() {
  saveMsg.value = '';
  if (!validateEdit()) return;
  saving.value = true;
  try {
    await updateUser({
      id: userStore.userInfo!.id,
      realName: editForm.realName || undefined,
      email: editForm.email || undefined,
      phone: editForm.phone || undefined,
    });
    await userStore.fetchUserInfo();
    saveMsg.value = '资料已更新';
    saveSuccess.value = true;
    setTimeout(() => { isEditing.value = false; saveMsg.value = ''; }, 1500);
  } catch (e: any) {
    saveMsg.value = e.message || '保存失败';
    saveSuccess.value = false;
  } finally {
    saving.value = false;
  }
}

async function handleLogout() {
  await userStore.logout();
  router.push('/');
}

onMounted(async () => {
  try {
    const [allRes, pubRes, pendRes] = await Promise.all([
      getMyCheckinList({ size: 1 }),
      getMyCheckinList({ size: 1, status: 1 }),
      getMyCheckinList({ size: 1, status: 0 }),
    ]);
    checkinStats[0].value = allRes.data.total;
    checkinStats[1].value = pubRes.data.total;
    checkinStats[2].value = pendRes.data.total;
  } catch {
    // ignore
  }
});
</script>
