<template>
  <main>
    <section class="relative overflow-hidden">
      <div class="absolute inset-0">
        <div class="absolute -top-32 -right-32 w-[520px] h-[520px] rounded-full bg-cyan-400/25 blur-3xl" />
        <div class="absolute -bottom-36 -left-36 w-[520px] h-[520px] rounded-full bg-emerald-400/15 blur-3xl" />
        <div
          class="absolute inset-0 opacity-[0.06]"
          style="background-image: radial-gradient(circle, #0ea5e9 1px, transparent 1px); background-size: 28px 28px"
        />
      </div>

      <div class="container-page relative py-14 sm:py-16">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          <div class="lg:col-span-7">
            <div class="inline-flex items-center gap-2 rounded-full px-3 py-1.5 glass">
              <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span class="text-sm text-slate-700">实时更新 · 热度榜</span>
            </div>

            <h1 class="mt-5 text-4xl sm:text-5xl font-bold tracking-tight text-slate-900">
              探索重庆
              <span class="block bg-gradient-to-r from-cyan-600 to-emerald-600 bg-clip-text text-transparent">
                记录每一次打卡
              </span>
            </h1>
            <p class="mt-4 text-slate-600 text-lg max-w-2xl">
              把美食、美景与故事留在这里。发布后会进入审核流程，通过后公开展示。
            </p>

            <div class="mt-7 flex flex-wrap gap-3">
              <router-link v-if="!userStore.isLoggedIn" to="/register" class="btn-primary px-5 py-2.5">
                <RocketIcon class="w-4 h-4" />
                立即加入
              </router-link>
              <router-link v-else to="/checkin/new" class="btn-primary px-5 py-2.5">
                <PlusCircleIcon class="w-4 h-4" />
                发起打卡
              </router-link>
              <button class="btn-secondary px-5 py-2.5" @click="scrollToRank">
                <TrendingUpIcon class="w-4 h-4" />
                查看热度榜
              </button>
            </div>

            <div class="mt-10 grid grid-cols-3 gap-3 max-w-xl">
              <div class="card p-4 text-center">
                <div class="text-2xl font-bold text-cyan-700">38+</div>
                <div class="text-xs text-slate-500 mt-1">区县</div>
              </div>
              <div class="card p-4 text-center">
                <div class="text-2xl font-bold text-cyan-700">8</div>
                <div class="text-xs text-slate-500 mt-1">美食种类</div>
              </div>
              <div class="card p-4 text-center">
                <div class="text-2xl font-bold text-cyan-700">10+</div>
                <div class="text-xs text-slate-500 mt-1">热门景点</div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-5">
            <div class="card p-5 sm:p-6">
              <div class="flex items-start justify-between">
                <div>
                  <div class="text-sm font-semibold text-slate-800">今日推荐</div>
                  <div class="mt-1 text-sm text-slate-600">从热度榜中挑一个就出发</div>
                </div>
                <div class="w-10 h-10 rounded-2xl bg-cyan-600 text-white flex items-center justify-center shadow-sm">
                  <SparklesIcon class="w-5 h-5" />
                </div>
              </div>

              <div class="mt-5 space-y-3">
                <div class="rounded-2xl border border-white/50 bg-white/60 p-4">
                  <div class="text-sm font-medium text-slate-800">美食</div>
                  <div class="mt-1 text-sm text-slate-600">看看大家都在打卡什么</div>
                </div>
                <div class="rounded-2xl border border-white/50 bg-white/60 p-4">
                  <div class="text-sm font-medium text-slate-800">美景</div>
                  <div class="mt-1 text-sm text-slate-600">挑选一个景点，记录路线与心情</div>
                </div>
              </div>

              <div class="mt-6 flex gap-3">
                <router-link to="/checkin/new" class="btn-primary flex-1" v-if="userStore.isLoggedIn">
                  <PlusIcon class="w-4 h-4" />
                  现在打卡
                </router-link>
                <router-link to="/login" class="btn-primary flex-1" v-else>
                  <LogInIcon class="w-4 h-4" />
                  登录开始
                </router-link>
                <router-link to="/my-checkins" class="btn-secondary flex-1" v-if="userStore.isLoggedIn">
                  <ClipboardListIcon class="w-4 h-4" />
                  我的记录
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section ref="rankSection" class="container-page py-10">
      <div class="card p-6">
        <div class="flex items-center justify-between gap-4">
          <div>
            <h2 class="text-lg font-semibold text-slate-900">热度榜</h2>
            <p class="text-sm text-slate-600 mt-1">区县 / 美食 / 景点三类热度排行（Top 10）。</p>
          </div>
          <div class="w-10 h-10 rounded-2xl bg-white/70 border border-white/40 flex items-center justify-center">
            <TrendingUpIcon class="w-5 h-5 text-cyan-700" />
          </div>
        </div>

        <div v-if="loading" class="mt-6 text-sm text-slate-500 flex items-center gap-2">
          <span class="w-4 h-4 rounded-full border-2 border-slate-200 border-t-cyan-600 animate-spin" />
          加载中...
        </div>

        <div v-else class="mt-6 grid grid-cols-1 lg:grid-cols-3 gap-4">
          <div class="rounded-2xl bg-white/60 border border-white/50 p-4">
            <div class="text-sm font-semibold text-slate-800 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-cyan-600" />
              区县热度
            </div>
            <div class="mt-3 space-y-2">
              <div
                v-for="(item, idx) in overview.districtRank"
                :key="item.id"
                class="flex items-center justify-between text-sm"
              >
                <div class="flex items-center gap-2 min-w-0">
                  <span class="w-6 text-slate-400 tabular-nums">{{ idx + 1 }}</span>
                  <span class="truncate text-slate-700">{{ item.name }}</span>
                </div>
                <span class="text-slate-500 tabular-nums">{{ item.count }}</span>
              </div>
              <div v-if="!overview.districtRank.length" class="text-sm text-slate-400">暂无数据</div>
            </div>
          </div>

          <div class="rounded-2xl bg-white/60 border border-white/50 p-4">
            <div class="text-sm font-semibold text-slate-800 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-amber-500" />
              美食热度
            </div>
            <div class="mt-3 space-y-2">
              <div v-for="(item, idx) in overview.foodTypeRank" :key="item.id" class="flex items-center justify-between text-sm">
                <div class="flex items-center gap-2 min-w-0">
                  <span class="w-6 text-slate-400 tabular-nums">{{ idx + 1 }}</span>
                  <span class="truncate text-slate-700">{{ item.name }}</span>
                </div>
                <span class="text-slate-500 tabular-nums">{{ item.count }}</span>
              </div>
              <div v-if="!overview.foodTypeRank.length" class="text-sm text-slate-400">暂无数据</div>
            </div>
          </div>

          <div class="rounded-2xl bg-white/60 border border-white/50 p-4">
            <div class="text-sm font-semibold text-slate-800 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-emerald-600" />
              景点热度
            </div>
            <div class="mt-3 space-y-2">
              <div
                v-for="(item, idx) in overview.scenicSpotRank"
                :key="item.id"
                class="flex items-center justify-between text-sm"
              >
                <div class="flex items-center gap-2 min-w-0">
                  <span class="w-6 text-slate-400 tabular-nums">{{ idx + 1 }}</span>
                  <span class="truncate text-slate-700">{{ item.name }}</span>
                </div>
                <span class="text-slate-500 tabular-nums">{{ item.count }}</span>
              </div>
              <div v-if="!overview.scenicSpotRank.length" class="text-sm text-slate-400">暂无数据</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { getHomeOverview } from '@/api/home';
import type { HomeOverview } from '@/api/home';
import {
  ClipboardList as ClipboardListIcon,
  LogIn as LogInIcon,
  Plus as PlusIcon,
  PlusCircle as PlusCircleIcon,
  Rocket as RocketIcon,
  Sparkles as SparklesIcon,
  TrendingUp as TrendingUpIcon,
} from 'lucide-vue-next';

const userStore = useUserStore();
const rankSection = ref<HTMLElement | null>(null);

const loading = ref(true);
const overview = reactive<HomeOverview>({
  districtRank: [],
  foodTypeRank: [],
  scenicSpotRank: [],
});

function scrollToRank() {
  rankSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

onMounted(async () => {
  try {
    const res = await getHomeOverview(10);
    Object.assign(overview, res.data);
  } catch {
    // ignore
  } finally {
    loading.value = false;
  }
});
</script>

