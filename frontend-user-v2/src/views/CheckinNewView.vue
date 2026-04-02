<template>
  <main class="container-page py-10 space-y-6">
    <div class="flex items-start justify-between gap-4">
      <div>
        <div class="text-sm text-slate-500">
          <router-link to="/" class="hover:text-cyan-700">首页</router-link>
          <span class="mx-2">/</span>
          <span class="text-slate-700">发起打卡</span>
        </div>
        <h1 class="mt-2 text-2xl font-bold text-slate-900 tracking-tight">发起打卡</h1>
        <p class="mt-1 text-slate-600">记录你在重庆的精彩时刻（每天最多 10 次）。</p>
      </div>
      <router-link to="/my-checkins" class="btn-secondary">我的打卡</router-link>
    </div>

    <form class="grid grid-cols-1 lg:grid-cols-12 gap-6" @submit.prevent="handleSubmit">
      <section class="lg:col-span-8 space-y-6">
        <div class="card p-6">
          <div class="flex items-center justify-between gap-4">
            <div>
              <div class="text-sm font-semibold text-slate-800">打卡类型</div>
              <div class="text-sm text-slate-600 mt-1">美食 / 美景（二选一）</div>
            </div>
            <div class="w-10 h-10 rounded-2xl bg-white/60 border border-white/50 flex items-center justify-center">
              <ShapesIcon class="w-5 h-5 text-cyan-700" />
            </div>
          </div>

          <div class="mt-5 grid grid-cols-2 gap-3">
            <button
              type="button"
              class="type-card"
              :class="form.type === 'food' ? 'type-card-active-amber' : 'type-card-idle'"
              @click="selectType('food')"
            >
              <div class="text-3xl">🍜</div>
              <div class="text-sm font-semibold">美食打卡</div>
              <div class="text-xs text-slate-500">火锅、小面、烤鱼...</div>
            </button>
            <button
              type="button"
              class="type-card"
              :class="form.type === 'scenic' ? 'type-card-active-sky' : 'type-card-idle'"
              @click="selectType('scenic')"
            >
              <div class="text-3xl">🏞</div>
              <div class="text-sm font-semibold">美景打卡</div>
              <div class="text-xs text-slate-500">洪崖洞、磁器口...</div>
            </button>
          </div>
        </div>

        <div class="card p-6 space-y-4">
          <div class="flex items-center justify-between gap-4">
            <div>
              <div class="text-sm font-semibold text-slate-800">基本信息</div>
              <div class="text-sm text-slate-600 mt-1">区县 + 标题必填</div>
            </div>
            <div class="w-10 h-10 rounded-2xl bg-white/60 border border-white/50 flex items-center justify-center">
              <FileTextIcon class="w-5 h-5 text-cyan-700" />
            </div>
          </div>

          <div>
            <label class="text-sm font-medium text-slate-700">所在区县 *</label>
            <select v-model="form.districtId" class="input mt-1.5" @change="onDistrictChange">
              <option :value="undefined" disabled>请选择区县</option>
              <option v-for="d in districts" :key="d.id" :value="d.id">{{ d.name }}</option>
            </select>
            <p v-if="errors.districtId" class="mt-1 text-xs text-red-600">{{ errors.districtId }}</p>
          </div>

          <div v-if="form.type === 'food'">
            <label class="text-sm font-medium text-slate-700">美食种类 *</label>
            <div class="mt-2 flex flex-wrap gap-2">
              <button
                v-for="ft in foodTypes"
                :key="ft.id"
                type="button"
                class="chip"
                :class="form.foodTypeId === ft.id ? 'chip-active-amber' : 'chip-idle'"
                @click="form.foodTypeId = ft.id"
              >
                {{ ft.name }}
              </button>
            </div>
            <p v-if="errors.foodTypeId" class="mt-1 text-xs text-red-600">{{ errors.foodTypeId }}</p>
          </div>

          <div v-else>
            <label class="text-sm font-medium text-slate-700">风景地</label>
            <select v-model="form.scenicSpotId" class="input mt-1.5">
              <option :value="undefined">不指定风景地</option>
              <option v-for="s in scenicSpots" :key="s.id" :value="s.id">{{ s.name }}</option>
            </select>
          </div>

          <div>
            <label class="text-sm font-medium text-slate-700">标题 *</label>
            <input
              v-model="form.title"
              class="input mt-1.5"
              placeholder="用一句话概括你的打卡体验..."
              maxlength="50"
              @input="errors.title = ''"
            />
            <div class="mt-1 flex items-center justify-between text-xs">
              <p v-if="errors.title" class="text-red-600">{{ errors.title }}</p>
              <p class="text-slate-400 ml-auto tabular-nums">{{ form.title.length }}/50</p>
            </div>
          </div>

          <div>
            <label class="text-sm font-medium text-slate-700">详细描述</label>
            <textarea
              v-model="form.content"
              class="input mt-1.5 min-h-[120px] resize-none"
              placeholder="描述一下你的体验、推荐理由、注意事项等..."
              maxlength="500"
            />
            <p class="mt-1 text-xs text-slate-400 text-right tabular-nums">{{ form.content.length }}/500</p>
          </div>
        </div>

        <div class="card p-6 space-y-4">
          <div class="flex items-center justify-between gap-4">
            <div>
              <div class="text-sm font-semibold text-slate-800">上传图片</div>
              <div class="text-sm text-slate-600 mt-1">最多 9 张，每张不超过 10MB</div>
            </div>
            <div class="w-10 h-10 rounded-2xl bg-white/60 border border-white/50 flex items-center justify-center">
              <ImageIcon class="w-5 h-5 text-cyan-700" />
            </div>
          </div>

          <div class="grid grid-cols-3 sm:grid-cols-4 gap-3">
            <div
              v-for="(img, idx) in uploadedImages"
              :key="idx"
              class="relative aspect-square rounded-2xl overflow-hidden bg-white/60 border border-white/50"
            >
              <img :src="img.preview" class="w-full h-full object-cover" alt="预览" />
              <div v-if="img.uploading" class="absolute inset-0 bg-black/30 flex items-center justify-center">
                <div class="w-6 h-6 rounded-full border-2 border-white border-t-transparent animate-spin" />
              </div>
              <button
                type="button"
                class="absolute top-2 right-2 w-8 h-8 rounded-xl bg-white/80 backdrop-blur border border-white/50 flex items-center justify-center hover:bg-white"
                @click="removeImage(idx)"
                aria-label="删除图片"
              >
                <XIcon class="w-4 h-4 text-slate-700" />
              </button>
            </div>

            <label
              v-if="uploadedImages.length < 9"
              class="aspect-square rounded-2xl border-2 border-dashed border-white/60 bg-white/40 hover:bg-white/60 transition-colors flex flex-col items-center justify-center gap-1 cursor-pointer"
            >
              <input type="file" accept="image/*" multiple class="hidden" @change="handleImageSelect" />
              <PlusIcon class="w-6 h-6 text-slate-400" />
              <div class="text-xs text-slate-500">添加图片</div>
            </label>
          </div>
        </div>
      </section>

      <aside class="lg:col-span-4 space-y-6">
        <div class="card p-6 space-y-4">
          <div class="flex items-center justify-between gap-4">
            <div>
              <div class="text-sm font-semibold text-slate-800">打卡位置</div>
              <div class="text-sm text-slate-600 mt-1">可选：地址 + 经纬度</div>
            </div>
            <div class="w-10 h-10 rounded-2xl bg-white/60 border border-white/50 flex items-center justify-center">
              <MapPinIcon class="w-5 h-5 text-cyan-700" />
            </div>
          </div>

          <button type="button" class="btn-secondary w-full" :disabled="locating" @click="getLocation">
            <LocateIcon class="w-4 h-4" :class="locating && 'animate-spin'" />
            {{ locating ? '定位中...' : '自动定位' }}
          </button>

          <div>
            <label class="text-sm font-medium text-slate-700">地址</label>
            <input v-model="form.address" class="input mt-1.5" placeholder="输入打卡地址（可选）" />
          </div>

          <div v-if="form.latitude && form.longitude" class="rounded-2xl bg-emerald-50 border border-emerald-200 px-4 py-3 text-xs text-emerald-800">
            已获取位置：<span class="tabular-nums">{{ form.latitude.toFixed(6) }}, {{ form.longitude.toFixed(6) }}</span>
          </div>
        </div>

        <div v-if="submitError" class="rounded-2xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700 flex items-start gap-2">
          <AlertCircleIcon class="w-4 h-4 mt-0.5" />
          <span>{{ submitError }}</span>
        </div>

        <div class="card p-6 space-y-3">
          <button class="btn-primary w-full py-2.5" type="submit" :disabled="submitting || uploadedImages.some(i => i.uploading)">
            <LoaderIcon v-if="submitting" class="w-4 h-4 animate-spin" />
            <SendIcon v-else class="w-4 h-4" />
            {{ submitting ? '提交中...' : '提交打卡' }}
          </button>
          <router-link to="/" class="btn-secondary w-full">取消</router-link>
          <p class="text-xs text-slate-500 leading-relaxed">
            提交后内容需经审核后才会公开展示。
          </p>
        </div>
      </aside>
    </form>

    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="showSuccess" class="fixed inset-0 z-50 bg-black/30 backdrop-blur-sm flex items-center justify-center p-4">
        <div class="card w-full max-w-sm p-7 text-center">
          <div class="w-16 h-16 rounded-3xl bg-emerald-500/10 text-emerald-700 mx-auto flex items-center justify-center">
            <CheckCircleIcon class="w-8 h-8" />
          </div>
          <h3 class="mt-4 text-lg font-semibold text-slate-900">打卡已提交</h3>
          <p class="mt-2 text-sm text-slate-600">你的内容正在等待审核，通过后将公开展示。</p>
          <div class="mt-6 flex gap-3">
            <router-link to="/my-checkins" class="btn-secondary flex-1" @click="showSuccess = false">查看记录</router-link>
            <button class="btn-primary flex-1" type="button" @click="resetForm">再次打卡</button>
          </div>
        </div>
      </div>
    </transition>
  </main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue';
import { addCheckin } from '@/api/checkin';
import { getDistrictList } from '@/api/district';
import { getFoodTypeList } from '@/api/foodType';
import { getScenicSpotList } from '@/api/scenicSpot';
import { uploadImage } from '@/api/upload';
import type { District } from '@/api/district';
import type { FoodType } from '@/api/foodType';
import type { ScenicSpot } from '@/api/scenicSpot';
import {
  AlertCircle as AlertCircleIcon,
  CheckCircle as CheckCircleIcon,
  Image as ImageIcon,
  Locate as LocateIcon,
  MapPin as MapPinIcon,
  Plus as PlusIcon,
  Send as SendIcon,
  Loader as LoaderIcon,
  X as XIcon,
} from 'lucide-vue-next';

interface UploadedImage {
  preview: string;
  url: string;
  uploading: boolean;
}

const form = reactive({
  districtId: undefined as number | undefined,
  type: 'food' as 'food' | 'scenic',
  foodTypeId: undefined as number | undefined,
  scenicSpotId: undefined as number | undefined,
  title: '',
  content: '',
  address: '',
  latitude: undefined as number | undefined,
  longitude: undefined as number | undefined,
});

const errors = reactive({
  districtId: '',
  foodTypeId: '',
  title: '',
});

const districts = ref<District[]>([]);
const foodTypes = ref<FoodType[]>([]);
const scenicSpots = ref<ScenicSpot[]>([]);

const uploadedImages = ref<UploadedImage[]>([]);
const locating = ref(false);
const submitting = ref(false);
const submitError = ref('');
const showSuccess = ref(false);

function selectType(type: 'food' | 'scenic') {
  form.type = type;
  form.foodTypeId = undefined;
  form.scenicSpotId = undefined;
  errors.foodTypeId = '';
}

async function onDistrictChange() {
  errors.districtId = '';
  if (form.type === 'scenic' && form.districtId) {
    const res = await getScenicSpotList(form.districtId);
    scenicSpots.value = res.data;
    form.scenicSpotId = undefined;
  }
}

watch(
  () => form.type,
  async (type) => {
    if (type === 'scenic') {
      const res = await getScenicSpotList(form.districtId);
      scenicSpots.value = res.data;
    }
  }
);

function validate() {
  let valid = true;
  if (!form.districtId) {
    errors.districtId = '请选择区县';
    valid = false;
  }
  if (form.type === 'food' && !form.foodTypeId) {
    errors.foodTypeId = '请选择美食种类';
    valid = false;
  }
  if (!form.title.trim()) {
    errors.title = '请填写标题';
    valid = false;
  } else {
    errors.title = '';
  }
  return valid;
}

async function handleImageSelect(e: Event) {
  const input = e.target as HTMLInputElement;
  const files = Array.from(input.files || []);
  const remaining = 9 - uploadedImages.value.length;
  const toUpload = files.slice(0, remaining);

  for (const file of toUpload) {
    const preview = URL.createObjectURL(file);
    const idx = uploadedImages.value.length;
    uploadedImages.value.push({ preview, url: '', uploading: true });
    try {
      const url = await uploadImage(file);
      uploadedImages.value[idx].url = url;
    } catch {
      uploadedImages.value[idx].url = preview;
    } finally {
      uploadedImages.value[idx].uploading = false;
    }
  }
  input.value = '';
}

function removeImage(idx: number) {
  uploadedImages.value.splice(idx, 1);
}

function getLocation() {
  if (!navigator.geolocation) return;
  locating.value = true;
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      form.latitude = pos.coords.latitude;
      form.longitude = pos.coords.longitude;
      locating.value = false;
    },
    () => {
      locating.value = false;
    },
    { timeout: 8000 }
  );
}

async function handleSubmit() {
  submitError.value = '';
  if (!validate()) return;
  submitting.value = true;
  try {
    const images = uploadedImages.value.map((i) => i.url).filter(Boolean).join(',');
    await addCheckin({
      districtId: form.districtId!,
      type: form.type,
      foodTypeId: form.type === 'food' ? form.foodTypeId : undefined,
      scenicSpotId: form.type === 'scenic' ? form.scenicSpotId : undefined,
      title: form.title,
      content: form.content || undefined,
      images: images || undefined,
      address: form.address || undefined,
      latitude: form.latitude,
      longitude: form.longitude,
    });
    showSuccess.value = true;
  } catch (e: any) {
    submitError.value = e?.message || '提交失败，请稍后再试';
  } finally {
    submitting.value = false;
  }
}

function resetForm() {
  showSuccess.value = false;
  form.districtId = undefined;
  form.type = 'food';
  form.foodTypeId = undefined;
  form.scenicSpotId = undefined;
  form.title = '';
  form.content = '';
  form.address = '';
  form.latitude = undefined;
  form.longitude = undefined;
  uploadedImages.value = [];
  errors.districtId = '';
  errors.foodTypeId = '';
  errors.title = '';
}

onMounted(async () => {
  const [dRes, fRes, sRes] = await Promise.all([getDistrictList(), getFoodTypeList(), getScenicSpotList()]);
  districts.value = dRes.data;
  foodTypes.value = fRes.data;
  scenicSpots.value = sRes.data;
});
</script>

<style scoped>
.type-card {
  @apply rounded-2xl border p-4 text-left transition-colors;
}
.type-card-idle {
  @apply bg-white/60 border-white/50 hover:bg-white/75;
}
.type-card-active-amber {
  @apply bg-amber-50 border-amber-200;
}
.type-card-active-sky {
  @apply bg-sky-50 border-sky-200;
}
.chip {
  @apply rounded-xl px-3 py-1.5 text-sm font-medium border transition-colors;
}
.chip-idle {
  @apply bg-white/70 border-white/50 text-slate-700 hover:bg-white;
}
.chip-active-amber {
  @apply bg-amber-500 text-white border-amber-500;
}
</style>
