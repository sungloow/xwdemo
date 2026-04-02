<template>
  <div>
    <!-- Hero Section -->
    <section class="relative overflow-hidden bg-gradient-to-br from-slate-900 via-primary-900 to-slate-800 text-white">
      <!-- Background elements -->
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute -top-20 -right-20 w-96 h-96 rounded-full bg-primary-600/20 blur-3xl" />
        <div class="absolute bottom-0 left-0 w-80 h-80 rounded-full bg-amber-500/10 blur-3xl" />
        <div class="absolute top-1/2 left-1/2 w-64 h-64 rounded-full bg-primary-500/10 blur-3xl" />
        <!-- Grid pattern -->
        <div class="absolute inset-0 opacity-5" style="background-image: radial-gradient(circle, white 1px, transparent 1px); background-size: 28px 28px;" />
      </div>

      <div class="relative max-w-6xl mx-auto px-4 sm:px-6 py-20 md:py-28">
        <div class="max-w-3xl">
          <div class="inline-flex items-center gap-2 px-3.5 py-1.5 bg-white/10 backdrop-blur-sm border border-white/20 rounded-full text-sm font-medium mb-6 animate-fade-in">
            <span class="w-2 h-2 rounded-full bg-green-400 animate-pulse" />
            实时更新 · 重庆城市打卡热度榜
          </div>
          <h1 class="text-4xl sm:text-5xl md:text-6xl font-bold leading-tight mb-5 animate-slide-up">
            探索重庆<br />
            <span class="bg-gradient-to-r from-amber-300 to-primary-400 bg-clip-text text-transparent">发现山城之美</span>
          </h1>
          <p class="text-lg text-white/70 mb-8 max-w-xl animate-slide-up" style="animation-delay: 0.1s">
            记录你在重庆的每一次美食探索与美景邂逅，与更多人分享属于你的山城故事
          </p>
          <div class="flex flex-wrap gap-4 animate-slide-up" style="animation-delay: 0.2s">
            <router-link v-if="!userStore.isLoggedIn" to="/register" class="btn-primary px-7 py-3 text-base">
              <ZapIcon class="w-4 h-4" />
              立即加入
            </router-link>
            <router-link v-else to="/checkin/new" class="btn-primary px-7 py-3 text-base">
              <PlusCircleIcon class="w-4 h-4" />
              发起打卡
            </router-link>
            <button @click="scrollToRank" class="inline-flex items-center gap-2 px-7 py-3 bg-white/10 hover:bg-white/20 border border-white/20 backdrop-blur-sm rounded-xl font-medium text-base transition-all">
              <TrendingUpIcon class="w-4 h-4" />
              查看热度榜
            </button>
          </div>
        </div>

        <!-- Stats bar -->
        <div class="grid grid-cols-3 gap-4 mt-12 max-w-xl">
          <div v-for="s in stats" :key="s.label" class="text-center p-4 bg-white/10 backdrop-blur-sm border border-white/10 rounded-2xl">
            <div class="text-2xl font-bold text-amber-300">{{ s.value }}</div>
            <div class="text-xs text-white/60 mt-0.5">{{ s.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <div class="max-w-6xl mx-auto px-4 sm:px-6 py-12 space-y-14" ref="rankSection">
      <!-- Loading state -->
      <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
        <div class="w-12 h-12 rounded-full border-4 border-slate-200 border-t-primary-500 animate-spin" />
        <p class="text-slate-400">加载热度数据中...</p>
      </div>

      <template v-else>
        <!-- District Rank -->
        <section class="animate-slide-up">
          <SectionHeader
            emoji="📍"
            title="区县打卡热度"
            subtitle="哪个区县最受欢迎？"
            :tag="{ text: '区县排行', color: 'primary' }"
          />
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mt-6">
            <RankCard
              v-for="(item, idx) in overview.districtRank"
              :key="item.id"
              :rank="idx + 1"
              :name="item.name"
              :count="item.count"
              unit="次打卡"
              :max-count="overview.districtRank[0]?.count || 1"
              color="primary"
            />
          </div>
          <div v-if="!overview.districtRank?.length" class="empty-state">
            <MapPinIcon class="w-10 h-10 text-slate-300" />
            <p class="text-slate-400">暂无数据</p>
          </div>
        </section>

        <!-- Food Rank -->
        <section class="animate-slide-up">
          <SectionHeader
            emoji="🍜"
            title="美食种类热度"
            subtitle="重庆哪种美食最火爆？"
            :tag="{ text: '美食排行', color: 'amber' }"
          />
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6">
            <FoodRankCard
              v-for="(item, idx) in overview.foodRank"
              :key="item.id"
              :rank="idx + 1"
              :name="item.name"
              :count="item.count"
              :max-count="overview.foodRank[0]?.count || 1"
            />
          </div>
          <div v-if="!overview.foodRank?.length" class="empty-state">
            <UtensilsIcon class="w-10 h-10 text-slate-300" />
            <p class="text-slate-400">暂无数据</p>
          </div>
        </section>

        <!-- Scenic Rank -->
        <section class="animate-slide-up">
          <SectionHeader
            emoji="🏞"
            title="美景热度排名"
            subtitle="最受打卡者青睐的景点"
            :tag="{ text: '景点排行', color: 'sky' }"
          />
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mt-6">
            <ScenicRankCard
              v-for="(item, idx) in overview.scenicRank"
              :key="item.id"
              :rank="idx + 1"
              :name="item.name"
              :count="item.count"
              :district="item.districtName"
              :max-count="overview.scenicRank[0]?.count || 1"
            />
          </div>
          <div v-if="!overview.scenicRank?.length" class="empty-state">
            <MountainSnowIcon class="w-10 h-10 text-slate-300" />
            <p class="text-slate-400">暂无数据</p>
          </div>
        </section>
      </template>
    </div>

    <!-- CTA Section -->
    <section v-if="!userStore.isLoggedIn" class="bg-gradient-to-r from-primary-600 to-primary-700 text-white py-16">
      <div class="max-w-4xl mx-auto px-6 text-center space-y-5">
        <h2 class="text-3xl font-bold">准备好开始你的山城探索了吗？</h2>
        <p class="text-primary-100 text-lg">注册账号，记录你的每一次重庆之旅</p>
        <div class="flex justify-center gap-4 pt-2">
          <router-link to="/register" class="px-8 py-3 bg-white text-primary-700 font-semibold rounded-xl hover:bg-primary-50 transition-all shadow-lg">
            免费注册
          </router-link>
          <router-link to="/login" class="px-8 py-3 bg-primary-500/50 hover:bg-primary-500/70 border border-primary-400 rounded-xl font-medium transition-all">
            已有账号？登录
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import {
  Zap as ZapIcon, PlusCircle as PlusCircleIcon, TrendingUp as TrendingUpIcon,
  MapPin as MapPinIcon, Utensils as UtensilsIcon, MountainSnow as MountainSnowIcon,
} from 'lucide-vue-next';
import { getHomeOverview } from '@/api/home';
import type { HomeOverview } from '@/api/home';
import { useUserStore } from '@/stores/user';
import SectionHeader from '@/components/SectionHeader.vue';
import RankCard from '@/components/RankCard.vue';
import FoodRankCard from '@/components/FoodRankCard.vue';
import ScenicRankCard from '@/components/ScenicRankCard.vue';

const userStore = useUserStore();
const loading = ref(true);
const rankSection = ref<HTMLElement | null>(null);

const overview = reactive<HomeOverview>({
  districtRank: [],
  foodRank: [],
  scenicRank: [],
});

const stats = [
  { value: '38+', label: '区县' },
  { value: '8', label: '美食种类' },
  { value: '10+', label: '热门景点' },
];

function scrollToRank() {
  rankSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

onMounted(async () => {
  try {
    const res = await getHomeOverview(10);
    Object.assign(overview, res.data);
  } catch (e) {
    console.error('Failed to load overview', e);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.empty-state {
  @apply flex flex-col items-center justify-center gap-2 py-10 text-center;
}
</style>
