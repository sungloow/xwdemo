<template>
  <div class="front-page">
    <header class="site-header">
      <div class="brand" @click="refreshAll">
        <span class="brand-mark">城</span>
        <div class="brand-copy">
          <strong>城市打卡</strong>
          <span>重庆</span>
        </div>
      </div>

      <div class="top-tabs">
        <button :class="{ active: activeTab === 'public' }" @click="switchTab('public')">发现</button>
        <button :class="{ active: activeTab === 'my' }" @click="switchTab('my')">我的</button>
      </div>

      <div class="header-actions">
        <el-button v-if="isAdmin" :icon="Monitor" @click="goAdmin">后台管理</el-button>
        <el-dropdown @command="handleUserCommand">
          <button class="user-menu">
            <span class="user-avatar">{{ displayName.slice(0, 1) }}</span>
            <span class="user-name">{{ displayName }}</span>
            <el-icon><ArrowDown /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="my">我的打卡</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <main class="front-shell">
      <section class="feed-section">
        <div class="filter-strip">
          <div v-if="activeTab === 'public'" class="chip-group">
            <button
              v-for="item in typeOptions"
              :key="item.value"
              class="chip"
              :class="{ active: publicQuery.type === item.value }"
              @click="changePublicType(item.value)"
            >
              {{ item.label }}
            </button>
          </div>
          <div v-else class="chip-group">
            <button
              v-for="item in myStatusOptions"
              :key="`my-${item.value}`"
              class="chip"
              :class="{ active: myQuery.status === item.value }"
              @click="changeMyStatus(item.value)"
            >
              {{ item.label }}
            </button>
          </div>

          <div class="feed-actions">
            <el-select
              v-if="activeTab === 'public'"
              v-model="publicQuery.districtId"
              clearable
              placeholder="全部区县"
              class="district-select"
              @change="loadPublicFeed(1)"
            >
              <el-option v-for="district in districts" :key="district.id" :label="district.name" :value="district.id" />
            </el-select>
            <el-button :icon="Refresh" circle @click="refreshCurrent" />
          </div>
        </div>

        <el-skeleton v-if="loading.feed" :rows="8" animated />

        <template v-else-if="activeTab === 'public'">
          <div v-if="publicRecords.length" class="masonry-grid">
            <article v-for="item in publicRecords" :key="item.id" class="feed-card clickable-card" @click="openDetail(item.id)">
              <div v-if="splitImages(item.images)[0]" class="cover-wrap">
                <el-image
                  class="cover-image"
                  :src="splitImages(item.images)[0]"
                  fit="cover"
                />
              </div>

              <div class="card-body">
                <h3 class="card-title">{{ item.title || '未命名打卡' }}</h3>
                <p v-if="item.content" class="card-desc">{{ item.content }}</p>

                <div class="meta-row">
                  <span class="meta-tag" :class="item.type === 'food' ? 'food' : 'scenic'">
                    {{ item.type === 'food' ? item.foodTypeName || '美食' : item.scenicSpotName || '美景' }}
                  </span>
                  <span v-if="item.districtName" class="meta-link">
                    <el-icon><MapLocation /></el-icon>{{ item.districtName }}
                  </span>
                </div>

                <div class="author-row">
                  <div class="author-avatar">{{ (item.username || displayName).slice(0, 1) }}</div>
                  <div class="author-info">
                    <strong>{{ item.username || displayName }}</strong>
                    <div class="author-subline">
                      <span class="author-time">{{ formatTime(item.createTime) }}</span>
                      <span class="author-stats">
                        <span>
                          <svg class="like-icon" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M4 10h3v9H4a1 1 0 0 1-1-1v-7a1 1 0 0 1 1-1Z" />
                            <path
                              d="M7 18.8h9.08a3.2 3.2 0 0 0 3.13-2.5l1.05-4.17A2.4 2.4 0 0 0 17.93 9H14.5l1.18-2.71c.5-1.25-.27-2.6-1.6-2.83-.46-.08-.93.14-1.18.54L9 10.2H7"
                            />
                          </svg>
                          {{ item.likeCount || 0 }}
                        </span>
                        <span><el-icon><ChatDotRound /></el-icon>{{ item.commentCount || 0 }}</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </article>
          </div>

          <el-empty v-else description="暂无打卡记录" />
        </template>

        <template v-else>
          <div v-if="myRecords.length" class="my-feed">
            <article v-for="item in myRecords" :key="item.id" class="my-card" @click="openMyDetail(item.id)">
              <div class="my-card-main">
                <div class="my-card-top">
                  <div>
                    <h3>{{ item.title || '未命名打卡' }}</h3>
                    <div class="my-meta">
                      <span>{{ item.districtName || '重庆' }}</span>
                      <span>{{ formatTime(item.createTime) }}</span>
                    </div>
                  </div>
                  <div class="my-tags">
                    <el-tag :type="item.type === 'food' ? 'warning' : 'success'" effect="light">
                      {{ item.type === 'food' ? '美食' : '美景' }}
                    </el-tag>
                    <el-tag :type="statusTagType(item.status)" effect="plain">
                      {{ statusText(item.status) }}
                    </el-tag>
                  </div>
                </div>

                <p v-if="item.content" class="my-card-desc">{{ item.content }}</p>

                <div v-if="splitImages(item.images).length" class="thumb-row">
                  <div
                    v-for="image in splitImages(item.images).slice(0, 3)"
                    :key="image"
                    @click.stop
                  >
                    <el-image
                      :src="image"
                      :preview-src-list="splitImages(item.images)"
                      fit="cover"
                    />
                  </div>
                </div>

                <div class="my-card-foot">
                  <span>{{ item.foodTypeName || item.scenicSpotName || item.address || '未填写地点' }}</span>
                  <span v-if="item.rejectReason" class="reject-reason">{{ item.rejectReason }}</span>
                </div>
              </div>
            </article>
          </div>

          <el-empty v-else description="暂无打卡记录" />
        </template>

        <div v-if="currentTotal > 0" class="pagination-row">
          <el-pagination
            background
            layout="prev, pager, next"
            :current-page="currentPage"
            :page-size="currentSize"
            :total="currentTotal"
            @current-change="handlePageChange"
          />
        </div>
      </section>

      <aside class="sidebar">
        <section class="trend-panel">
          <div class="panel-title">
            <div>
              <strong>{{ activeRankPanel.title }}</strong>
              <span>每周热门打卡趋势</span>
            </div>
            <span class="panel-badge">
              <el-icon><TrendCharts /></el-icon>
              HOT
            </span>
          </div>
          <div class="rank-tabs">
            <button
              v-for="rank in rankPanels"
              :key="rank.key"
              :class="{ active: activeRankTab === rank.key }"
              @click="activeRankTab = rank.key"
            >
              {{ rank.shortTitle }}
            </button>
          </div>
          <ol class="trend-list">
            <li v-for="(item, index) in activeRankPanel.items" :key="`${activeRankPanel.key}-${item.id}`">
              <span class="trend-index" :class="`rank-${index + 1}`">{{ String(index + 1).padStart(2, '0') }}</span>
              <div class="trend-main">
                <span class="trend-name">{{ item.name }}</span>
                <span class="trend-sub">{{ activeRankPanel.shortTitle }}持续升温</span>
              </div>
              <span class="trend-count">{{ item.count }}</span>
            </li>
          </ol>
        </section>
      </aside>
    </main>

    <button class="publish-fab" type="button" @click="openAddDrawer">
      <el-icon><Plus /></el-icon>
      <span>发布</span>
    </button>

    <AddCheckinDrawer ref="addDrawerRef" />
    <CheckinDetailDialog ref="detailDialogRef" @changed="handleDetailChanged" />
    <MyCheckinDetailDialog ref="myDetailDialogRef" @edit="openEditDrawer" />
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessageBox } from 'element-plus';
  import { ArrowDown, ChatDotRound, MapLocation, Monitor, Plus, Refresh, TrendCharts } from '@element-plus/icons-vue';
  import { getDistrictList, getHomeOverview, getMyCheckinList, getPublishedCheckins } from '@/apis';
  import { useAuthStore } from '@/store/auth';
  import { useUserStore } from '@/store/user';
  import AddCheckinDrawer from '@/views/checkin/my/AddCheckinDrawer.vue';
  import MyCheckinDetailDialog from '@/views/front/MyCheckinDetailDialog.vue';
  import CheckinDetailDialog from '@/views/front/CheckinDetailDialog.vue';

  const router = useRouter();
  const userStore: any = useUserStore();
  const authStore: any = useAuthStore();
  const addDrawerRef = ref();
  const detailDialogRef = ref();
  const myDetailDialogRef = ref();

  const activeTab = ref<'public' | 'my'>('public');
  const loading = reactive({ feed: false, rank: false });
  const districts = ref<any[]>([]);
  const publicRecords = ref<any[]>([]);
  const myRecords = ref<any[]>([]);
  const publicTotal = ref(0);
  const myTotal = ref(0);
  const activeRankTab = ref<'district' | 'food' | 'scenic'>('district');
  const overview = reactive({
    districtRank: [] as any[],
    foodTypeRank: [] as any[],
    scenicSpotRank: [] as any[],
  });
  const typeOptions = [
    { label: '全部', value: '' as '' | 'food' | 'scenic' },
    { label: '美食', value: 'food' as '' | 'food' | 'scenic' },
    { label: '美景', value: 'scenic' as '' | 'food' | 'scenic' },
  ];
  const myStatusOptions = [
    { label: '全部', value: '' as '' | 0 | 1 | 2 },
    { label: '待审核', value: 0 as '' | 0 | 1 | 2 },
    { label: '已通过', value: 1 as '' | 0 | 1 | 2 },
    { label: '已拒绝', value: 2 as '' | 0 | 1 | 2 },
  ];
  const publicQuery = reactive({
    current: 1,
    size: 10,
    type: '' as '' | 'food' | 'scenic',
    districtId: undefined as number | undefined,
  });
  const myQuery = reactive({
    current: 1,
    size: 8,
    status: '' as '' | 0 | 1 | 2,
  });

  const displayName = computed(() => userStore.userInfo?.roleName || userStore.userInfo?.username || '用户');
  const isAdmin = computed(() => {
    const roles = authStore.authInfo?.roles || [];
    return authStore.authInfo?.isSuper === 1 || roles.includes('SUPER_ADMIN') || roles.includes('DISTRICT_ADMIN');
  });
  const rankPanels = computed(() => [
    { key: 'district', title: '区县热度榜', shortTitle: '区县', items: overview.districtRank },
    { key: 'food', title: '美食热度榜', shortTitle: '美食', items: overview.foodTypeRank },
    { key: 'scenic', title: '美景热度榜', shortTitle: '美景', items: overview.scenicSpotRank },
  ]);
  const activeRankPanel = computed(() => {
    return rankPanels.value.find((item) => item.key === activeRankTab.value) || rankPanels.value[0];
  });
  const currentTotal = computed(() => activeTab.value === 'public' ? publicTotal.value : myTotal.value);
  const currentPage = computed(() => activeTab.value === 'public' ? publicQuery.current : myQuery.current);
  const currentSize = computed(() => activeTab.value === 'public' ? publicQuery.size : myQuery.size);

  onMounted(async () => {
    await Promise.all([loadDistricts(), loadOverview(), loadPublicFeed(1)]);
  });

  const loadDistricts = async () => {
    const res: any = await getDistrictList();
    districts.value = res.data || [];
  };

  const loadOverview = async () => {
    loading.rank = true;
    try {
      const res: any = await getHomeOverview(8);
      overview.districtRank = res.data?.districtRank || [];
      overview.foodTypeRank = res.data?.foodTypeRank || [];
      overview.scenicSpotRank = res.data?.scenicSpotRank || [];
    } finally {
      loading.rank = false;
    }
  };

  const loadPublicFeed = async (page = publicQuery.current) => {
    loading.feed = true;
    publicQuery.current = page;
    try {
      const res: any = await getPublishedCheckins({
        current: publicQuery.current,
        size: publicQuery.size,
        type: publicQuery.type || undefined,
        districtId: publicQuery.districtId,
      });
      publicRecords.value = res.data?.records || [];
      publicTotal.value = res.data?.total || 0;
    } finally {
      loading.feed = false;
    }
  };

  const loadMyFeed = async (page = myQuery.current) => {
    loading.feed = true;
    myQuery.current = page;
    try {
      const res: any = await getMyCheckinList({
        current: myQuery.current,
        size: myQuery.size,
        status: myQuery.status === '' ? undefined : myQuery.status,
      });
      myRecords.value = res.data?.records || [];
      myTotal.value = res.data?.total || 0;
    } finally {
      loading.feed = false;
    }
  };

  const switchTab = (tab: 'public' | 'my') => {
    activeTab.value = tab;
    if (tab === 'public') {
      loadPublicFeed(1);
    } else {
      loadMyFeed(1);
    }
  };

  const changePublicType = (value: '' | 'food' | 'scenic') => {
    if (activeTab.value !== 'public') activeTab.value = 'public';
    publicQuery.type = value;
    loadPublicFeed(1);
  };

  const changeMyStatus = (value: '' | 0 | 1 | 2) => {
    if (activeTab.value !== 'my') activeTab.value = 'my';
    myQuery.status = value;
    loadMyFeed(1);
  };

  const refreshCurrent = () => {
    activeTab.value === 'public' ? loadPublicFeed(1) : loadMyFeed(1);
  };

  const refreshAll = async () => {
    await Promise.all([
      loadOverview(),
      loadDistricts(),
      activeTab.value === 'public' ? loadPublicFeed(1) : loadMyFeed(1),
    ]);
  };

  const handlePageChange = (page: number) => {
    activeTab.value === 'public' ? loadPublicFeed(page) : loadMyFeed(page);
  };

  const openAddDrawer = () => {
    addDrawerRef.value.acceptParams({
      title: '发布打卡',
      getTableList: async () => {
        await Promise.all([loadOverview(), loadPublicFeed(1), loadMyFeed(1)]);
      },
    });
  };

  const openDetail = (id: number) => {
    detailDialogRef.value?.open(id);
  };

  const openMyDetail = (id: number) => {
    myDetailDialogRef.value?.open(id);
  };

  const openEditDrawer = (detail: any) => {
    addDrawerRef.value.acceptParams({
      title: '编辑打卡',
      mode: 'edit',
      rowData: detail,
      getTableList: async () => {
        await Promise.all([loadOverview(), loadPublicFeed(1), loadMyFeed(1)]);
      },
    });
  };

  const handleDetailChanged = (payload: { id: number; likeCount: number; commentCount: number; liked: boolean }) => {
    publicRecords.value = publicRecords.value.map((item) => {
      if (item.id !== payload.id) return item;
      return {
        ...item,
        likeCount: payload.likeCount,
        commentCount: payload.commentCount,
        liked: payload.liked,
      };
    });
    myRecords.value = myRecords.value.map((item) => {
      if (item.id !== payload.id) return item;
      return {
        ...item,
        likeCount: payload.likeCount,
        commentCount: payload.commentCount,
        liked: payload.liked,
      };
    });
  };

  const goAdmin = () => {
    router.push('/index');
  };

  const handleUserCommand = async (command: string) => {
    if (command === 'my') {
      switchTab('my');
      return;
    }
    if (command === 'logout') {
      await ElMessageBox.confirm('您确定要退出吗？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      });
      await userStore.Logout();
      router.push('/login');
    }
  };

  const splitImages = (images?: string) => {
    return (images || '').split(',').map((item) => item.trim()).filter(Boolean).slice(0, 6);
  };

  const formatTime = (value?: string) => {
    if (!value) return '';
    return value.replace('T', ' ').slice(0, 16);
  };

  const statusText = (status: number) => {
    if (status === 0) return '待审核';
    if (status === 1) return '已通过';
    return '已拒绝';
  };

  const statusTagType = (status: number) => {
    if (status === 0) return 'info';
    if (status === 1) return 'success';
    return 'danger';
  };
</script>

<style scoped lang="scss">
  .front-page {
    min-height: 100vh;
    color: #1f2937;
    background:
      radial-gradient(circle at 12% 0%, rgba(255, 92, 122, 0.14), transparent 28%),
      linear-gradient(180deg, #fff6f7 0%, #fff 180px),
      linear-gradient(180deg, #fff 0%, #fff8f6 100%);
  }

  .site-header {
    position: sticky;
    top: 0;
    z-index: 30;
    display: grid;
    grid-template-columns: auto 1fr auto;
    align-items: center;
    gap: 24px;
    height: 72px;
    padding: 0 clamp(18px, 4vw, 52px);
    border-bottom: 1px solid rgba(17, 24, 39, 0.08);
    backdrop-filter: blur(18px);
    background: rgba(255, 255, 255, 0.9);
  }

  .brand,
  .header-actions,
  .user-menu,
  .profile-head,
  .author-row,
  .author-subline,
  .meta-row,
  .my-card-top,
  .my-meta,
  .my-card-foot,
  .panel-title,
  .trend-list li {
    display: flex;
    align-items: center;
  }

  .brand {
    gap: 12px;
    cursor: pointer;
  }

  .brand-mark,
  .user-avatar,
  .profile-avatar,
  .author-avatar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .brand-mark {
    width: 40px;
    height: 40px;
    border-radius: 14px;
    font-weight: 700;
    background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
  }

  .brand-copy {
    strong,
    span {
      display: block;
      line-height: 1.2;
    }

    strong {
      font-size: 18px;
    }

    span {
      color: #9ca3af;
      font-size: 12px;
    }
  }

  .top-tabs {
    display: inline-flex;
    justify-self: center;
    padding: 4px;
    border-radius: 999px;
    background: #f3f4f6;

    button {
      min-width: 88px;
      height: 38px;
      padding: 0 18px;
      border: 0;
      border-radius: 999px;
      color: #6b7280;
      background: transparent;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.2s ease;
    }

    button.active {
      color: #1f2937;
      background: #fff;
      box-shadow: 0 8px 18px rgba(255, 92, 122, 0.12);
    }
  }

  .header-actions {
    justify-content: flex-end;
    gap: 12px;
  }

  .user-menu {
    gap: 8px;
    height: 40px;
    padding: 0 10px 0 4px;
    border: 1px solid #ececec;
    border-radius: 999px;
    color: #374151;
    background: #fff;
    cursor: pointer;
  }

  .user-avatar,
  .author-avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
  }

  .front-shell {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 312px;
    gap: 24px;
    width: min(1220px, calc(100% - 32px));
    margin: 0 auto;
    padding: 24px 0 48px;
  }

  .feed-section {
    min-width: 0;
  }

  .filter-strip {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 18px;
    padding: 4px 0;
  }

  .chip-group,
  .feed-actions,
  .my-tags {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .chip {
    height: 36px;
    padding: 0 16px;
    border: 1px solid #eceff3;
    border-radius: 999px;
    color: #4b5563;
    background: #fff;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .chip.active {
    color: #fff;
    border-color: #ff5c7a;
    background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
    box-shadow: 0 10px 22px rgba(255, 92, 122, 0.18);
  }

  .district-select {
    width: 148px;
  }

  .masonry-grid {
    columns: 3 220px;
    column-gap: 14px;
  }

  .feed-card,
  .my-card,
  .trend-panel {
    border: 1px solid #f2e4e8;
    border-radius: 16px;
    background: #fff;
    box-shadow: 0 12px 32px rgba(31, 41, 55, 0.05);
  }

  .feed-card {
    break-inside: avoid;
    margin-bottom: 14px;
    overflow: hidden;
  }

  .clickable-card {
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 18px 36px rgba(31, 41, 55, 0.08);
    }
  }

  .cover-wrap {
    overflow: hidden;
    background: #f3f4f6;
  }

  .cover-image {
    width: 100%;
    aspect-ratio: 4 / 5;
    display: block;
  }

  .card-body {
    padding: 12px 12px 14px;
  }

  .card-title {
    margin: 0;
    font-size: 15px;
    line-height: 1.45;
    color: #111827;
  }

  .card-desc {
    display: -webkit-box;
    margin: 8px 0 0;
    overflow: hidden;
    color: #4b5563;
    line-height: 1.65;
    font-size: 13px;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }

  .meta-row {
    gap: 10px;
    flex-wrap: wrap;
    margin-top: 12px;
  }

  .meta-tag,
  .meta-link {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
  }

  .meta-tag {
    height: 26px;
    padding: 0 10px;
    border-radius: 999px;
    background: #fff1f4;
  }

  .meta-tag.food {
    color: #ff5c7a;
    background: #fff1f4;
  }

  .meta-tag.scenic {
    color: #ff8d6b;
    background: #fff4ef;
  }

  .meta-link {
    color: #6b7280;
  }

  .author-row {
    gap: 10px;
    margin-top: 12px;
    padding-top: 10px;
    border-top: 1px solid #f3f4f6;
  }

  .author-info {
    strong,
    .author-subline {
      display: block;
      line-height: 1.2;
    }
  }

  .author-subline {
    gap: 14px;
    margin-top: 4px;
    flex-wrap: wrap;
    color: #9ca3af;
    font-size: 12px;

    span {
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
  }

  .author-time {
    flex-shrink: 0;
  }

  .author-stats {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    padding-left: 10px;
    border-left: 1px solid #ebeef2;
  }

  .author-stats .like-icon {
    width: 13px;
    height: 13px;
    fill: none;
    stroke: currentColor;
    stroke-width: 1.8;
    stroke-linecap: round;
    stroke-linejoin: round;
  }

  .my-feed {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .my-card {
    padding: 18px;
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 18px 36px rgba(31, 41, 55, 0.08);
    }
  }

  .my-card-top {
    justify-content: space-between;
    align-items: flex-start;
    gap: 14px;

    h3 {
      margin: 0;
      font-size: 18px;
      line-height: 1.4;
    }
  }

  .my-meta,
  .my-card-foot {
    gap: 10px;
    flex-wrap: wrap;
    margin-top: 8px;
    color: #6b7280;
    font-size: 13px;
  }

  .my-card-desc {
    margin: 14px 0 0;
    color: #374151;
    line-height: 1.8;
    white-space: pre-wrap;
  }

  .thumb-row {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
    margin-top: 14px;

    > div {
      min-width: 0;
    }

    :deep(.el-image) {
      width: 100%;
      aspect-ratio: 1 / 1;
      border-radius: 8px;
      overflow: hidden;
    }
  }

  .reject-reason {
    color: #dc2626;
  }

  .pagination-row {
    display: flex;
    justify-content: center;
    margin-top: 22px;
  }

  .sidebar {
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .trend-panel {
    padding: 18px;
  }

  .panel-title {
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 14px;

    strong,
    span {
      display: block;
    }

    strong {
      color: #1f2937;
      font-size: 16px;
    }

    span {
      margin-top: 4px;
      color: #9ca3af;
      font-size: 12px;
    }
  }

  .panel-badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    height: 28px;
    padding: 0 10px;
    border-radius: 999px;
    color: #ff5c7a;
    background: #fff1f4;
    font-size: 12px;
    font-weight: 700;
  }

  .rank-tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 14px;

    button {
      flex: 1;
      height: 34px;
      border: 1px solid #f2e4e8;
      border-radius: 999px;
      color: #6b7280;
      background: #fff;
      cursor: pointer;
      font-size: 13px;
      transition: all 0.2s ease;
    }

    button.active {
      color: #ff5c7a;
      border-color: #ffd8e0;
      background: #fff1f4;
      font-weight: 700;
    }
  }

  .trend-list {
    padding: 0;
    margin: 0;
    list-style: none;
  }

  .trend-list li:last-child {
    margin-bottom: 0;
  }

  .trend-list li {
    gap: 12px;
    padding: 12px;
    margin-bottom: 10px;
    border: 1px solid #f6e8eb;
    border-radius: 14px;
    background: linear-gradient(180deg, #fff 0%, #fff8fa 100%);
  }

  .trend-index {
    width: 38px;
    height: 38px;
    border-radius: 12px;
    color: #6b7280;
    background: #f8fafc;
    text-align: center;
    line-height: 38px;
    font-size: 13px;
    font-weight: 800;
    flex-shrink: 0;
  }

  .trend-index.rank-1 {
    color: #fff;
    background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
  }

  .trend-index.rank-2 {
    color: #fff;
    background: linear-gradient(135deg, #ff8d6b, #f1c94a);
  }

  .trend-index.rank-3 {
    color: #fff;
    background: linear-gradient(135deg, #f1c94a, #ffd86b);
  }

  .trend-main {
    flex: 1;
    min-width: 0;
  }

  .trend-sub {
    display: block;
    margin-top: 5px;
    color: #9ca3af;
    font-size: 12px;
  }

  .trend-name {
    display: block;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #1f2937;
    font-weight: 600;
  }

  .trend-count {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 34px;
    height: 28px;
    padding: 0 10px;
    border-radius: 999px;
    color: #ff5c7a;
    background: #fff1f4;
    font-weight: 700;
    font-size: 12px;
  }

  .publish-fab {
    position: fixed;
    right: max(18px, calc((100vw - 1220px) / 2 + 18px));
    bottom: 30px;
    z-index: 24;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    height: 52px;
    padding: 0 18px;
    border: 0;
    border-radius: 999px;
    color: #fff;
    background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
    box-shadow: 0 18px 36px rgba(255, 92, 122, 0.24);
    cursor: pointer;
    font-size: 15px;
    font-weight: 700;
  }

  @media (max-width: 1080px) {
    .site-header {
      grid-template-columns: 1fr auto;
      height: auto;
      padding-top: 14px;
      padding-bottom: 14px;
    }

    .top-tabs {
      order: 3;
      grid-column: 1 / -1;
      justify-self: start;
    }

    .front-shell {
      grid-template-columns: 1fr;
    }

    .publish-fab {
      right: 18px;
      bottom: 24px;
    }
  }

  @media (max-width: 720px) {
    .header-actions {
      gap: 8px;
    }

    .user-name {
      display: none;
    }

    .masonry-grid {
      columns: 1;
    }

    .filter-strip {
      flex-direction: column;
      align-items: stretch;
    }

    .feed-actions {
      justify-content: space-between;
    }

    .publish-fab {
      right: 16px;
      bottom: 18px;
      height: 48px;
      padding: 0 16px;
    }
  }
</style>
