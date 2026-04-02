<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 py-10 space-y-8">
    <!-- Header -->
    <div class="space-y-1">
      <div class="flex items-center gap-2 text-sm text-slate-400 mb-3">
        <router-link to="/" class="hover:text-primary-600 transition-colors">首页</router-link>
        <span>/</span>
        <span class="text-slate-600">发起打卡</span>
      </div>
      <h1 class="text-2xl font-bold text-slate-900">发起打卡</h1>
      <p class="text-slate-500">记录你在重庆的精彩时刻，每天最多可打卡 <span class="font-semibold text-primary-600">10</span> 次</p>
    </div>

    <form @submit.prevent="handleSubmit" class="space-y-6">
      <!-- Type selection -->
      <div class="card p-6 space-y-4">
        <div class="flex items-center gap-2 text-sm font-semibold text-slate-500 uppercase tracking-wider">
          <TypeIcon class="w-4 h-4" />
          打卡类型
        </div>
        <div class="grid grid-cols-2 gap-4">
          <button
            type="button"
            @click="form.type = 'food'; form.foodTypeId = undefined; form.scenicSpotId = undefined"
            class="relative flex flex-col items-center gap-3 p-5 rounded-2xl border-2 transition-all duration-200"
            :class="form.type === 'food'
              ? 'border-amber-400 bg-amber-50 shadow-sm'
              : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50'"
          >
            <div class="text-4xl">🍜</div>
            <div class="text-center">
              <div class="font-semibold" :class="form.type === 'food' ? 'text-amber-700' : 'text-slate-700'">美食打卡</div>
              <div class="text-xs mt-0.5" :class="form.type === 'food' ? 'text-amber-500' : 'text-slate-400'">火锅、小面、烤鱼...</div>
            </div>
            <div v-if="form.type === 'food'" class="absolute top-3 right-3 w-5 h-5 rounded-full bg-amber-400 flex items-center justify-center">
              <CheckIcon class="w-3 h-3 text-white" />
            </div>
          </button>
          <button
            type="button"
            @click="form.type = 'scenic'; form.foodTypeId = undefined; form.scenicSpotId = undefined"
            class="relative flex flex-col items-center gap-3 p-5 rounded-2xl border-2 transition-all duration-200"
            :class="form.type === 'scenic'
              ? 'border-sky-400 bg-sky-50 shadow-sm'
              : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50'"
          >
            <div class="text-4xl">🏞</div>
            <div class="text-center">
              <div class="font-semibold" :class="form.type === 'scenic' ? 'text-sky-700' : 'text-slate-700'">美景打卡</div>
              <div class="text-xs mt-0.5" :class="form.type === 'scenic' ? 'text-sky-500' : 'text-slate-400'">洪崖洞、磁器口...</div>
            </div>
            <div v-if="form.type === 'scenic'" class="absolute top-3 right-3 w-5 h-5 rounded-full bg-sky-400 flex items-center justify-center">
              <CheckIcon class="w-3 h-3 text-white" />
            </div>
          </button>
        </div>
      </div>

      <!-- Basic Info -->
      <div class="card p-6 space-y-5">
        <div class="flex items-center gap-2 text-sm font-semibold text-slate-500 uppercase tracking-wider">
          <InfoIcon class="w-4 h-4" />
          基本信息
        </div>

        <!-- District -->
        <div class="space-y-1.5">
          <label class="text-sm font-medium text-slate-700">所在区县 <span class="text-red-500">*</span></label>
          <select v-model="form.districtId" class="input-field" :class="errors.districtId && 'border-red-400 focus:ring-red-400'" @change="onDistrictChange">
            <option :value="undefined" disabled>请选择区县</option>
            <option v-for="d in districts" :key="d.id" :value="d.id">{{ d.name }}</option>
          </select>
          <p v-if="errors.districtId" class="text-xs text-red-500">{{ errors.districtId }}</p>
        </div>

        <!-- Food type selector -->
        <div v-if="form.type === 'food'" class="space-y-1.5">
          <label class="text-sm font-medium text-slate-700">美食种类 <span class="text-red-500">*</span></label>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="ft in foodTypes" :key="ft.id"
              type="button"
              @click="form.foodTypeId = ft.id"
              class="px-3.5 py-1.5 rounded-xl text-sm font-medium border transition-all duration-150"
              :class="form.foodTypeId === ft.id
                ? 'bg-amber-500 text-white border-amber-500 shadow-sm'
                : 'bg-white text-slate-600 border-slate-200 hover:border-amber-300 hover:text-amber-600'"
            >
              {{ ft.name }}
            </button>
          </div>
          <p v-if="errors.foodTypeId" class="text-xs text-red-500">{{ errors.foodTypeId }}</p>
        </div>

        <!-- Scenic spot selector -->
        <div v-if="form.type === 'scenic'" class="space-y-1.5">
          <label class="text-sm font-medium text-slate-700">
            风景地
            <span class="text-xs text-slate-400 font-normal ml-1">（先选择区县以筛选）</span>
          </label>
          <select v-model="form.scenicSpotId" class="input-field">
            <option :value="undefined">不指定风景地</option>
            <option v-for="s in scenicSpots" :key="s.id" :value="s.id">{{ s.name }}</option>
          </select>
        </div>

        <!-- Title -->
        <div class="space-y-1.5">
          <label class="text-sm font-medium text-slate-700">标题 <span class="text-red-500">*</span></label>
          <input
            v-model="form.title"
            type="text"
            placeholder="用一句话概括你的打卡体验..."
            class="input-field"
            maxlength="50"
            :class="errors.title && 'border-red-400 focus:ring-red-400'"
            @input="errors.title = ''"
          />
          <div class="flex justify-between">
            <p v-if="errors.title" class="text-xs text-red-500">{{ errors.title }}</p>
            <span class="text-xs text-slate-400 ml-auto">{{ form.title.length }}/50</span>
          </div>
        </div>

        <!-- Content -->
        <div class="space-y-1.5">
          <label class="text-sm font-medium text-slate-700">详细描述</label>
          <textarea
            v-model="form.content"
            placeholder="描述一下你的打卡体验，推荐理由，注意事项等..."
            class="input-field resize-none"
            rows="4"
            maxlength="500"
          />
          <span class="text-xs text-slate-400 text-right block">{{ form.content?.length || 0 }}/500</span>
        </div>
      </div>

      <!-- Image Upload -->
      <div class="card p-6 space-y-4">
        <div class="flex items-center gap-2 text-sm font-semibold text-slate-500 uppercase tracking-wider">
          <ImageIcon class="w-4 h-4" />
          上传图片 <span class="text-xs font-normal text-slate-400 normal-case ml-1">最多9张，每张不超过10MB</span>
        </div>

        <div class="grid grid-cols-3 sm:grid-cols-4 gap-3">
          <!-- Image previews -->
          <div
            v-for="(img, idx) in uploadedImages"
            :key="idx"
            class="relative aspect-square rounded-xl overflow-hidden bg-slate-100 group"
          >
            <img :src="img.preview" class="w-full h-full object-cover" alt="preview" />
            <div class="absolute inset-0 bg-black/0 group-hover:bg-black/30 transition-all duration-200" />
            <button
              type="button"
              @click="removeImage(idx)"
              class="absolute top-1.5 right-1.5 w-6 h-6 rounded-full bg-red-500 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity shadow-lg hover:bg-red-600"
            >
              <XIcon class="w-3.5 h-3.5" />
            </button>
            <div v-if="img.uploading" class="absolute inset-0 bg-black/40 flex items-center justify-center">
              <div class="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin" />
            </div>
          </div>

          <!-- Upload button -->
          <label
            v-if="uploadedImages.length < 9"
            class="aspect-square rounded-xl border-2 border-dashed border-slate-200 hover:border-primary-400 hover:bg-primary-50 flex flex-col items-center justify-center gap-1 cursor-pointer transition-all duration-200 group"
          >
            <input type="file" accept="image/*" multiple class="hidden" @change="handleImageSelect" />
            <PlusIcon class="w-6 h-6 text-slate-300 group-hover:text-primary-500 transition-colors" />
            <span class="text-xs text-slate-400 group-hover:text-primary-500 transition-colors">添加图片</span>
          </label>
        </div>
      </div>

      <!-- Location -->
      <div class="card p-6 space-y-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2 text-sm font-semibold text-slate-500 uppercase tracking-wider">
            <MapPinIcon class="w-4 h-4" />
            打卡位置
          </div>
          <button
            type="button"
            @click="getLocation"
            :disabled="locating"
            class="flex items-center gap-1.5 px-3 py-1.5 text-sm text-sky-600 hover:bg-sky-50 rounded-lg transition-colors font-medium"
          >
            <LocateIcon class="w-4 h-4" :class="locating && 'animate-spin'" />
            {{ locating ? '定位中...' : '自动定位' }}
          </button>
        </div>
        <input
          v-model="form.address"
          type="text"
          placeholder="输入打卡地址，或点击自动定位"
          class="input-field"
        />
        <div v-if="form.latitude && form.longitude" class="flex items-center gap-2 px-3 py-2 bg-green-50 border border-green-200 rounded-xl text-xs text-green-700">
          <CheckCircleIcon class="w-4 h-4 shrink-0" />
          已获取位置：{{ form.latitude.toFixed(6) }}, {{ form.longitude.toFixed(6) }}
        </div>
      </div>

      <!-- Error / Success -->
      <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 -translate-y-2" enter-to-class="opacity-100 translate-y-0">
        <div v-if="submitError" class="flex items-center gap-2.5 p-4 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
          <AlertCircleIcon class="w-4 h-4 shrink-0" />
          {{ submitError }}
        </div>
      </transition>

      <!-- Actions -->
      <div class="flex gap-4">
        <router-link to="/" class="btn-secondary flex-1 py-3">取消</router-link>
        <button type="submit" class="btn-primary flex-1 py-3 text-base" :disabled="submitting || uploadedImages.some(i => i.uploading)">
          <LoaderIcon v-if="submitting" class="w-4 h-4 animate-spin" />
          <SendIcon v-else class="w-4 h-4" />
          {{ submitting ? '提交中...' : '提交打卡' }}
        </button>
      </div>
      <p class="text-center text-xs text-slate-400">提交后内容需经审核后才会公开展示</p>
    </form>

    <!-- Success Modal -->
    <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0" enter-to-class="opacity-100">
      <div v-if="showSuccess" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-sm p-8 text-center space-y-5 animate-scale-in">
          <div class="w-20 h-20 rounded-full bg-green-100 flex items-center justify-center mx-auto">
            <CheckCircleIcon class="w-10 h-10 text-green-500" />
          </div>
          <div class="space-y-2">
            <h3 class="text-xl font-bold text-slate-800">打卡成功！</h3>
            <p class="text-slate-500 text-sm">你的打卡内容已提交，正在等待审核，审核通过后将公开展示</p>
          </div>
          <div class="flex gap-3">
            <router-link to="/my-checkins" class="btn-secondary flex-1">查看记录</router-link>
            <button @click="resetForm" class="btn-primary flex-1">再次打卡</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import {
  Check as CheckIcon, Info as InfoIcon, Type as TypeIcon, Image as ImageIcon,
  MapPin as MapPinIcon, Locate as LocateIcon, Plus as PlusIcon, X as XIcon,
  Send as SendIcon, Loader as LoaderIcon, AlertCircle as AlertCircleIcon,
  CheckCircle as CheckCircleIcon,
} from 'lucide-vue-next';
import { addCheckin } from '@/api/checkin';
import { getDistrictList } from '@/api/district';
import { getFoodTypeList } from '@/api/foodType';
import { getScenicSpotList } from '@/api/scenicSpot';
import { uploadImage } from '@/api/upload';
import type { District } from '@/api/district';
import type { FoodType } from '@/api/foodType';
import type { ScenicSpot } from '@/api/scenicSpot';

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

async function onDistrictChange() {
  if (form.type === 'scenic' && form.districtId) {
    const res = await getScenicSpotList(form.districtId);
    scenicSpots.value = res.data;
    form.scenicSpotId = undefined;
  }
}

watch(() => form.type, async (type) => {
  if (type === 'scenic') {
    if (form.districtId) {
      const res = await getScenicSpotList(form.districtId);
      scenicSpots.value = res.data;
    } else {
      const res = await getScenicSpotList();
      scenicSpots.value = res.data;
    }
  }
});

function validate() {
  let valid = true;
  if (!form.districtId) { errors.districtId = '请选择区县'; valid = false; }
  if (form.type === 'food' && !form.foodTypeId) { errors.foodTypeId = '请选择美食种类'; valid = false; }
  if (!form.title.trim()) { errors.title = '请填写标题'; valid = false; }
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
    () => { locating.value = false; },
    { timeout: 8000 }
  );
}

async function handleSubmit() {
  submitError.value = '';
  if (!validate()) return;
  submitting.value = true;
  try {
    const images = uploadedImages.value.map(i => i.url).filter(Boolean).join(',');
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
    submitError.value = e.message || '提交失败，请稍后再试';
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
  Object.assign(errors, { districtId: '', foodTypeId: '', title: '' });
}

onMounted(async () => {
  const [dRes, fRes, sRes] = await Promise.all([
    getDistrictList(),
    getFoodTypeList(),
    getScenicSpotList(),
  ]);
  districts.value = dRes.data;
  foodTypes.value = fRes.data;
  scenicSpots.value = sRes.data;
});
</script>
