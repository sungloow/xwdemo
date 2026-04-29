<template>
  <div class="page-home">
    <el-row :gutter="20">

      <el-col :span="8">
        <clock-dial/>
        <el-card class="profile-box">
          <template #header>
            <div class="card-header">
              <span>我的档案</span>
            </div>
          </template>
          <div class="user-content">
            <div class="avatar-box">
              <img class="user-avatar" src="../../assets/images/avatar.png" alt=""/>
            </div>
            <div class="item-box">
              <div class="name">姓名：{{user.username}}</div>
              <div>角色：{{user.roleName}}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-row :gutter="20">
          <!-- 区县热度排名表 -->
          <el-col :span="24">
            <el-card class="rank-box">
              <template #header>
                <div class="card-header">
                  <span>区县热度排名表</span>
                </div>
              </template>
              <div class="rank-content">
                <el-loading :loading="loading.district" element-loading-text="加载中...">
                  <div class="district-list">
                    <div v-for="(district, index) in districtRankData" :key="district.id" class="district-item">
                      <div class="district-rank" :class="{
                        'district-rank-first': index === 0,
                        'district-rank-second': index === 1,
                        'district-rank-third': index === 2
                      }">{{index + 1}}</div>
                      <div class="district-info">
                        <div class="district-name">{{district.name}}</div>
                      </div>
                      <div class="district-heat">
                        <span class="heat-value">{{district.count}}</span>
                      </div>
                    </div>
                  </div>
                </el-loading>
              </div>
            </el-card>
          </el-col>

          <!-- 美食按种类展示热度排名 -->
          <el-col :span="12">
            <el-card class="rank-box">
              <template #header>
                <div class="card-header">
                  <span>美食热度排名</span>
                </div>
              </template>
              <div class="rank-content">
                <el-loading :loading="loading.food" element-loading-text="加载中...">
                  <div class="food-list">
                    <div v-for="(food, index) in foodRankData" :key="food.id" class="food-item">
                      <div class="food-rank" :class="{
                            'food-rank-first': index === 0,
                            'food-rank-second': index === 1,
                            'food-rank-third': index === 2
                          }">{{index + 1}}</div>
                      <div class="food-info">
                        <div class="food-name">{{food.name}}</div>
                      </div>
                      <div class="food-heat">
                        <span class="heat-value">{{food.count}}</span>
                      </div>
                    </div>
                  </div>
                </el-loading>
              </div>
            </el-card>
          </el-col>

          <!-- 美景热度排名 -->
          <el-col :span="12">
            <el-card class="rank-box">
              <template #header>
                <div class="card-header">
                  <span>美景热度排名</span>
                </div>
              </template>
              <div class="rank-content">
                <el-loading :loading="loading.scenic" element-loading-text="加载中...">
                  <div class="scenic-list">
                    <div v-for="(spot, index) in scenicSpotRankData" :key="spot.id" class="scenic-item">
                      <div class="scenic-rank" :class="{
                        'scenic-rank-first': index === 0,
                        'scenic-rank-second': index === 1,
                        'scenic-rank-third': index === 2
                      }">{{index + 1}}</div>
                      <div class="scenic-info">
                        <div class="scenic-name">{{spot.name}}</div>
                      </div>
                      <div class="scenic-heat">
                        <span class="heat-value">{{spot.count}}</span>
                      </div>
                    </div>
                  </div>
                </el-loading>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>

</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { useUserStore } from '@/store/user';
  import { getHomeOverview, getDistrictRank, getFoodRank, getScenicRank } from '@/apis';

  const userStore: any = useUserStore();
  const user = userStore.userInfo;

  // 区县热度排名数据
  const districtRankData = ref([]);
  // 美食分类数据
  const foodCategories = ref([]);
  // 美食热度排名数据
  const foodRankData = ref([]);
  // 美景热度排名数据
  const scenicSpotRankData = ref([]);
  // 激活的美食标签
  const activeFoodTab = ref('');
  // 加载状态
  const loading = ref({
    district: false,
    food: false,
    scenic: false
  });

  onMounted(async () => {
    await fetchData();
  });


  // 获取数据
  const fetchData = async () => {
    try {
      // 获取区县热度排名
      loading.value.district = true;
      try {
        const districtRes = await getDistrictRank(10);
        console.log('districtRes',districtRes);
        districtRankData.value = districtRes.data ;
      } catch (error) {
        console.error('获取区县热度排名失败:', error);

      }

      // 获取美食热度排名
      loading.value.food = true;
      try {
        const foodRes = await getFoodRank(10);
        console.log('---foodRes.data',foodRes.data)
        foodRankData.value = foodRes.data ;
      } catch (error) {
        console.error('获取美食热度排名失败:', error);
        activeFoodTab.value = '1';
      }

      // 获取美景热度排名
      loading.value.scenic = true;
      try {
        const scenicRes = await getScenicRank(10);
        scenicSpotRankData.value = scenicRes.data ;
      } catch (error) {
        console.error('获取美景热度排名失败:', error);

      }
    } catch (error) {
      console.error('获取数据失败:', error);
    } finally {
      loading.value.district = false;
      loading.value.food = false;
      loading.value.scenic = false;
    }
  };

</script>

<style lang="scss" scoped>

  .page-home {
    height: 100vh;
    background: #fff;
  }

  ::v-deep(.el-card__header) {
    background: url('@/assets/images/card_header.png');
    background-size: cover;
  }

  .profile-box {
    margin-top: 15px;

    .user-content {
      display: flex;

      .avatar-box {
        width: 120px;

        .user-avatar {
          width: 90px;
          height: 90px;
        }
      }

      .item-box {
        line-height: 30px;
        font-size: 14px;
        color: #999;

        .name {
          font-size: 16px;
          color: #333;
        }
      }
    }
  }

  .rank-box {
    margin-top: 10px;

    .rank-content {
      padding: 10px;

      .el-table {
        font-size: 12px;
        border-radius: 8px;
        overflow: hidden;
        max-height: 200px;

        .rank-badge {
          display: inline-block;
          width: 24px;
          height: 24px;
          line-height: 24px;
          text-align: center;
          border-radius: 50%;
          font-weight: bold;
          font-size: 12px;
          color: #fff;
          background-color: #909399;

          &.rank-badge-first {
            background-color: #f56c6c;
          }

          &.rank-badge-second {
            background-color: #e6a23c;
          }

          &.rank-badge-third {
            background-color: #409eff;
          }
        }

        .heat-container {
          display: flex;
          align-items: center;
          width: 100%;
          height: 16px;

          .heat-bar {
            flex: 1;
            height: 6px;
            background-color: #f0f0f0;
            border-radius: 3px;
            overflow: hidden;
            margin-right: 8px;

            &::after {
              content: '';
              display: block;
              height: 100%;
              background-color: #409eff;
              border-radius: 3px;
            }
          }

          .heat-value {
            font-weight: bold;
            color: #409eff;
            min-width: 30px;
            text-align: right;
            font-size: 12px;
          }
        }
      }

      .food-tabs {
        .el-tabs__header {
          margin-bottom: 10px;
          .el-tabs__nav {
            height: 32px;
            .el-tabs__item {
              font-size: 12px;
              padding: 0 10px;
            }
          }
        }
      }

      .food-list {
        max-height: 180px;
        overflow-y: auto;

        .food-item {
          display: flex;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .food-rank {
            display: inline-block;
            width: 24px;
            height: 24px;
            line-height: 24px;
            text-align: center;
            border-radius: 50%;
            font-weight: bold;
            font-size: 12px;
            color: #fff;
            background-color: #909399;
            margin-right: 10px;

            &.food-rank-first {
              background-color: #f56c6c;
            }

            &.food-rank-second {
              background-color: #e6a23c;
            }

            &.food-rank-third {
              background-color: #409eff;
            }
          }

          .food-info {
            flex: 1;

            .food-name {
              font-size: 12px;
              font-weight: bold;
              color: #333;
              margin-bottom: 2px;
            }

            .food-category {
              font-size: 10px;
              color: #999;
            }
          }

          .food-heat {
            width: 80px;

            .heat-bar {
              height: 6px;
              background-color: #f0f0f0;
              border-radius: 3px;
              overflow: hidden;
              margin-bottom: 2px;

              &::after {
                content: '';
                display: block;
                height: 100%;
                background-color: #f56c6c;
                border-radius: 3px;
              }
            }

            .heat-value {
              font-size: 10px;
              font-weight: bold;
              color: #f56c6c;
              text-align: right;
            }
          }
        }
      }

      .district-list {
        max-height: 180px;
        overflow-y: auto;

        .district-item {
          display: flex;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .district-rank {
            display: inline-block;
            width: 24px;
            height: 24px;
            line-height: 24px;
            text-align: center;
            border-radius: 50%;
            font-weight: bold;
            font-size: 12px;
            color: #fff;
            background-color: #909399;
            margin-right: 10px;

            &.district-rank-first {
              background-color: #f56c6c;
            }

            &.district-rank-second {
              background-color: #e6a23c;
            }

            &.district-rank-third {
              background-color: #409eff;
            }
          }

          .district-info {
            flex: 1;

            .district-name {
              font-size: 12px;
              font-weight: bold;
              color: #333;
              margin-bottom: 2px;
            }
          }

          .district-heat {
            width: 80px;

            .heat-bar {
              height: 6px;
              background-color: #f0f0f0;
              border-radius: 3px;
              overflow: hidden;
              margin-bottom: 2px;

              &::after {
                content: '';
                display: block;
                height: 100%;
                background-color: #409eff;
                border-radius: 3px;
              }
            }

            .heat-value {
              font-size: 10px;
              font-weight: bold;
              color: #409eff;
              text-align: right;
            }
          }
        }
      }

      .scenic-list {
        max-height: 180px;
        overflow-y: auto;

        .scenic-item {
          display: flex;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .scenic-rank {
            display: inline-block;
            width: 24px;
            height: 24px;
            line-height: 24px;
            text-align: center;
            border-radius: 50%;
            font-weight: bold;
            font-size: 12px;
            color: #fff;
            background-color: #909399;
            margin-right: 10px;

            &.scenic-rank-first {
              background-color: #f56c6c;
            }

            &.scenic-rank-second {
              background-color: #e6a23c;
            }

            &.scenic-rank-third {
              background-color: #409eff;
            }
          }

          .scenic-info {
            flex: 1;

            .scenic-name {
              font-size: 12px;
              font-weight: bold;
              color: #333;
              margin-bottom: 2px;
            }

            .scenic-district {
              font-size: 10px;
              color: #999;
            }
          }

          .scenic-heat {
            width: 80px;

            .heat-bar {
              height: 6px;
              background-color: #f0f0f0;
              border-radius: 3px;
              overflow: hidden;
              margin-bottom: 2px;

              &::after {
                content: '';
                display: block;
                height: 100%;
                background-color: #409eff;
                border-radius: 3px;
              }
            }

            .heat-value {
              font-size: 10px;
              font-weight: bold;
              color: #409eff;
              text-align: right;
            }
          }
        }
      }
    }
  }

</style>
