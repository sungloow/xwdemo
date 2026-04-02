<template>
  <main class="container-page py-10 space-y-6">
    <div class="flex items-center gap-3">
      <router-link to="/" class="w-9 h-9 rounded-xl bg-white/50 border border-white/50 hover:bg-white/70 transition-colors flex items-center justify-center">
        <ArrowLeftIcon class="w-4 h-4 text-slate-700" />
      </router-link>
      <div>
        <h1 class="text-2xl font-bold text-slate-900 tracking-tight">个人中心</h1>
        <p class="text-sm text-slate-600">管理资料与查看统计</p>
      </div>
    </div>

    <section class="card overflow-hidden">
      <div class="h-28 bg-gradient-to-r from-cyan-600 via-cyan-500 to-emerald-500 relative">
        <div
          class="absolute inset-0 opacity-[0.18]"
          style="background-image: radial-gradient(circle, white 1px, transparent 1px); background-size: 20px 20px"
        />
      </div>
      <div class="p-6">
        <div class="flex items-end justify-between gap-4 -mt-12">
          <div class="w-20 h-20 rounded-3xl bg-white/70 backdrop-blur border border-white/50 shadow-sm flex items-center justify-center">
            <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-cyan-500 to-cyan-700 text-white flex items-center justify-center text-2xl font-bold">
              {{ userStore.displayName.charAt(0).toUpperCase() }}
            </div>
          </div>
          <button class="btn-secondary" @click="toggleEdit">
            <component :is="isEditing ? XIcon : EditIcon" class="w-4 h-4" />
            {{ isEditing ? '取消' : '编辑资料' }}
          </button>
        </div>

        <div v-if="!isEditing" class="mt-4">
          <div class="text-xl font-bold text-slate-900">{{ userStore.displayName }}</div>
          <div class="text-sm text-slate-500 mt-1">@{{ userStore.userInfo?.username }}</div>

          <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div class="rounded-2xl bg-white/60 border border-white/50 p-4 flex items-center gap-3">
              <MailIcon class="w-4 h-4 text-slate-500" />
              <div class="text-sm text-slate-700 truncate">{{ userStore.userInfo?.email || '未填写邮箱' }}</div>
            </div>
            <div class="rounded-2xl bg-white/60 border border-white/50 p-4 flex items-center gap-3">
              <PhoneIcon class="w-4 h-4 text-slate-500" />
              <div class="text-sm text-slate-700 truncate">{{ userStore.userInfo?.phone || '未填写手机号' }}</div>
            </div>
          </div>
        </div>

        <form v-else class="mt-5 space-y-4 max-w-2xl" @submit.prevent="handleSave">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="text-sm font-medium text-slate-700">真实姓名</label>
              <input v-model="editForm.realName" class="input mt-1.5" placeholder="请输入真实姓名" />
            </div>
            <div>
              <label class="text-sm font-medium text-slate-700">手机号</label>
              <div class="relative mt-1.5">
                <PhoneIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input v-model="editForm.phone" class="input pl-10" placeholder="11位手机号" @input="editErrors.phone = ''" />
              </div>
              <p v-if="editErrors.phone" class="mt-1 text-xs text-red-600">{{ editErrors.phone }}</p>
            </div>
            <div class="sm:col-span-2">
              <label class="text-sm font-medium text-slate-700">邮箱</label>
              <div class="relative mt-1.5">
                <MailIcon class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input v-model="editForm.email" class="input pl-10" placeholder="邮箱地址" @input="editErrors.email = ''" />
              </div>
              <p v-if="editErrors.email" class="mt-1 text-xs text-red-600">{{ editErrors.email }}</p>
            </div>
          </div>

          <div v-if="saveMsg" class="rounded-2xl px-4 py-3 text-sm flex items-center gap-2"
            :class="saveSuccess ? 'bg-emerald-50 text-emerald-800 border border-emerald-200' : 'bg-red-50 text-red-700 border border-red-200'">
            <component :is="saveSuccess ? CheckCircleIcon : AlertCircleIcon" class="w-4 h-4" />
            {{ saveMsg }}
          </div>

          <div class="flex gap-3 pt-1">
            <button type="button" class="btn-secondary flex-1" @click="toggleEdit">取消</button>
            <button type="submit" class="btn-primary flex-1" :disabled="saving">
              <LoaderIcon v-if="saving" class="w-4 h-4 animate-spin" />
              {{ saving ? '保存中...' : '保存修改' }}
            </button>
          </div>
        </form>
      </div>
    </section>

    <section class="grid grid-cols-3 gap-3">
      <div class="card p-5 text-center">
        <div class="text-3xl font-bold text-slate-900 tabular-nums">{{ stats.all }}</div>
        <div class="text-sm text-slate-600 mt-1">全部打卡</div>
      </div>
      <div class="card p-5 text-center">
        <div class="text-3xl font-bold text-emerald-700 tabular-nums">{{ stats.published }}</div>
        <div class="text-sm text-slate-600 mt-1">已发布</div>
      </div>
      <div class="card p-5 text-center">
        <div class="text-3xl font-bold text-amber-700 tabular-nums">{{ stats.pending }}</div>
        <div class="text-sm text-slate-600 mt-1">待审核</div>
      </div>
    </section>

    <section class="card p-6 space-y-3">
      <div class="text-sm font-semibold text-slate-800">快捷操作</div>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <router-link to="/checkin/new" class="action">
          <div class="w-10 h-10 rounded-2xl bg-cyan-600/10 text-cyan-700 flex items-center justify-center">📍</div>
          <div>
            <div class="text-sm font-medium text-slate-800">发起打卡</div>
            <div class="text-xs text-slate-500">记录新的精彩</div>
          </div>
        </router-link>
        <router-link to="/my-checkins" class="action">
          <div class="w-10 h-10 rounded-2xl bg-emerald-600/10 text-emerald-700 flex items-center justify-center">📋</div>
          <div>
            <div class="text-sm font-medium text-slate-800">我的打卡</div>
            <div class="text-xs text-slate-500">查看历史记录</div>
          </div>
        </router-link>
      </div>
    </section>

    <section class="card p-6 border-red-100 space-y-3">
      <div class="text-sm font-semibold text-red-600">退出登录</div>
      <p class="text-sm text-slate-600">退出后需要重新登录才能使用打卡功能。</p>
      <button class="btn-secondary text-red-600 border-red-200 hover:bg-red-50" @click="handleLogout">
        <LogOutIcon class="w-4 h-4" />
        退出登录
      </button>
    </section>
  </main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { updateUser } from '@/api/user';
import { getMyCheckinList } from '@/api/checkin';
import {
  AlertCircle as AlertCircleIcon,
  ArrowLeft as ArrowLeftIcon,
  CheckCircle as CheckCircleIcon,
  Edit as EditIcon,
  Loader as LoaderIcon,
  LogOut as LogOutIcon,
  Mail as MailIcon,
  Phone as PhoneIcon,
  X as XIcon,
} from 'lucide-vue-next';

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

const stats = reactive({
  all: 0,
  published: 0,
  pending: 0,
});

function toggleEdit() {
  isEditing.value = !isEditing.value;
  saveMsg.value = '';
  editErrors.phone = '';
  editErrors.email = '';
  if (isEditing.value) {
    editForm.realName = userStore.userInfo?.realName || '';
    editForm.email = userStore.userInfo?.email || '';
    editForm.phone = userStore.userInfo?.phone || '';
  }
}

function validateEdit() {
  let valid = true;
  if (editForm.phone && !/^1[3-9]\\d{9}$/.test(editForm.phone)) {
    editErrors.phone = '手机号格式不正确';
    valid = false;
  }
  if (editForm.email && !/^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/.test(editForm.email)) {
    editErrors.email = '邮箱格式不正确';
    valid = false;
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
    setTimeout(() => {
      isEditing.value = false;
      saveMsg.value = '';
    }, 1000);
  } catch (e: any) {
    saveMsg.value = e?.message || '保存失败';
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
  await userStore.fetchUserInfo();
  try {
    const [allRes, pubRes, pendRes] = await Promise.all([
      getMyCheckinList({ size: 1 }),
      getMyCheckinList({ size: 1, status: 1 }),
      getMyCheckinList({ size: 1, status: 0 }),
    ]);
    stats.all = allRes.data.total;
    stats.published = pubRes.data.total;
    stats.pending = pendRes.data.total;
  } catch {
    // ignore
  }
});
</script>

<style scoped>
.action {
  @apply flex items-center gap-3 rounded-2xl border border-white/50 bg-white/50 hover:bg-white/70 transition-colors p-4;
}
</style>
