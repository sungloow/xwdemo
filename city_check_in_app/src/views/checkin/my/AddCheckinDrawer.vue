<template>
  <el-dialog
    v-model="drawerVisible"
    :destroy-on-close="true"
    :show-close="false"
    :align-center="true"
    width="680px"
    class="add-checkin-dialog"
  >
    <div class="drawer-shell">
      <div class="drawer-header">
        <div>
          <div class="drawer-eyebrow">Share Your City</div>
          <h2>{{ drawerProps.title || '发布打卡' }}</h2>
          <p>像发一篇笔记一样，把这一刻的重庆记录下来。</p>
        </div>
        <button class="close-btn" type="button" @click="drawerVisible = false">
          <el-icon><Close /></el-icon>
        </button>
      </div>

      <div class="drawer-body">
        <el-form
          ref="ruleFormRef"
          :rules="rules"
          :model="formData"
          label-position="top"
          class="checkin-form"
        >
          <section class="hero-section">
            <div class="hero-copy">
              <span class="hero-badge">{{ formData.type === 'food' ? '美食打卡' : '美景打卡' }}</span>
              <h3>先放一张图，让这篇打卡有封面感</h3>
              <p>支持多张图片，默认会按上传顺序展示。</p>
            </div>

            <el-form-item prop="images" class="cover-form-item">
              <el-upload
                action=""
                multiple
                accept="image/*"
                :show-file-list="false"
                :auto-upload="false"
                :disabled="imageUploading"
                :on-change="handleImageChange"
                class="cover-upload"
              >
                <div class="cover-dropzone">
                  <el-icon class="cover-icon"><PictureFilled /></el-icon>
                  <strong>{{ imageUploading ? '图片上传中...' : '上传封面 / 图集' }}</strong>
                  <span>支持 jpg、png、gif、webp，单张不超过 10MB</span>
                </div>
              </el-upload>

              <div v-if="imageList.length" class="image-grid">
                <div v-for="(image, index) in imageList" :key="image" class="image-card">
                  <el-image :src="image" :preview-src-list="imageList" fit="cover" />
                  <button class="delete-image" type="button" @click="removeImage(index)">
                    <el-icon><Delete /></el-icon>
                  </button>
                  <span v-if="index === 0" class="cover-chip">封面</span>
                </div>
              </div>
            </el-form-item>
          </section>

          <section class="form-section">
            <div class="section-title">这是一篇什么打卡</div>
            <el-form-item label="打卡类型" prop="type">
              <div class="type-toggle">
                <button
                  type="button"
                  class="type-option"
                  :class="{ active: formData.type === 'food' }"
                  @click="setType('food')"
                >
                  美食
                </button>
                <button
                  type="button"
                  class="type-option"
                  :class="{ active: formData.type === 'scenic' }"
                  @click="setType('scenic')"
                >
                  美景
                </button>
              </div>
            </el-form-item>

            <div class="form-grid">
              <el-form-item label="所属区县" prop="districtId">
                <el-select v-model="formData.districtId" placeholder="请选择所属区县" clearable>
                  <el-option
                    v-for="district in districts"
                    :key="district.id"
                    :label="district.name"
                    :value="district.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item v-if="formData.type === 'food'" label="美食类型" prop="foodTypeId">
                <el-select v-model="formData.foodTypeId" placeholder="请选择美食类型" clearable>
                  <el-option
                    v-for="foodType in foodTypes"
                    :key="foodType.id"
                    :label="foodType.name"
                    :value="foodType.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item v-else label="景点" prop="scenicSpotId">
                <el-select
                  v-model="formData.scenicSpotId"
                  :placeholder="formData.districtId ? '请选择景点' : '请先选择所属区县'"
                  clearable
                  :disabled="!formData.districtId"
                >
                  <el-option
                    v-for="scenicSpot in filteredScenicSpots"
                    :key="scenicSpot.id"
                    :label="scenicSpot.name"
                    :value="scenicSpot.id"
                  />
                </el-select>
              </el-form-item>
            </div>
          </section>

          <section class="form-section">
            <div class="section-title">把内容写得像一篇笔记</div>

            <el-form-item label="打卡标题" prop="title">
              <el-input
                v-model="formData.title"
                maxlength="50"
                show-word-limit
                placeholder="例如：解放碑旁边这家火锅太值得排队了"
                clearable
              />
            </el-form-item>

            <el-form-item label="打卡内容" prop="content">
              <el-input
                v-model="formData.content"
                type="textarea"
                :rows="6"
                maxlength="200"
                show-word-limit
                resize="none"
                placeholder="写下你的感受、推荐理由、避坑提醒，越真实越有分享感。"
              />
            </el-form-item>

            <el-form-item label="打卡地址" prop="address">
              <el-input
                v-model="formData.address"
                maxlength="100"
                show-word-limit
                placeholder="例如：渝中区解放碑八一路 66 号"
                clearable
              />
            </el-form-item>
          </section>
        </el-form>
      </div>

      <div class="drawer-footer">
        <div class="footer-tip">发布后会进入审核队列，通过后会出现在首页动态里。</div>
        <div class="footer-actions">
          <el-button class="footer-cancel-btn" @click="drawerVisible = false">取消</el-button>
          <el-button class="footer-submit-btn" type="primary" @click="handleSubmit" :loading="loading || imageUploading">
            立即发布
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts" name="AddCheckinDrawer">
  import { computed, onMounted, reactive, ref, watch } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { FormInstance } from 'element-plus';
  import { Close, Delete, PictureFilled } from '@element-plus/icons-vue';
  import { addCheckin, getDistrictList, getFoodTypeList, getScenicSpotList, uploadImage } from '@/apis';
  import { clearEmptyValue, textRule } from '@/utils';

  interface DrawerProps {
    title: string
    api?: (params: any) => Promise<any>
    getTableList?: () => Promise<any>
  }

  const rules = reactive({
    type: { required: true, message: '请选择打卡类型', trigger: 'change' },
    districtId: { required: true, message: '请选择所属区县', trigger: 'change' },
    foodTypeId: {
      required: true,
      message: '请选择美食类型',
      trigger: 'change',
      validator: (rule: any, value: any, callback: any) => {
        if (formData.value.type === 'food' && !value) callback(new Error('请选择美食类型'));
        else callback();
      },
    },
    scenicSpotId: {
      required: true,
      message: '请选择景点',
      trigger: 'change',
      validator: (rule: any, value: any, callback: any) => {
        if (formData.value.type === 'scenic' && !value) callback(new Error('请选择景点'));
        else callback();
      },
    },
    title: {
      required: true,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 50),
      trigger: 'blur',
    },
    content: {
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 200),
      trigger: 'blur',
    },
    address: {
      required: false,
      validator: (rule: any, value: any, callback: any) => textRule(rule, value, callback, 100),
      trigger: 'blur',
    },
  });

  const ruleFormRef = ref<FormInstance>();
  const drawerVisible = ref(false);
  const drawerProps = ref<DrawerProps>({ title: '' });
  const formData = ref({
    type: 'food',
    districtId: '',
    foodTypeId: '',
    scenicSpotId: '',
    title: '',
    content: '',
    images: '',
    address: '',
    latitude: '',
    longitude: '',
  });
  const imageList = ref<string[]>([]);
  const imageUploading = ref(false);
  const districts = ref<any[]>([]);
  const foodTypes = ref<any[]>([]);
  const scenicSpots = ref<any[]>([]);
  const loading = ref(false);
  const filteredScenicSpots = computed(() => {
    if (!formData.value.districtId) return [];
    return scenicSpots.value.filter((item) => item.districtId === formData.value.districtId);
  });

  const resetForm = () => {
    formData.value = {
      type: 'food',
      districtId: '',
      foodTypeId: '',
      scenicSpotId: '',
      title: '',
      content: '',
      images: '',
      address: '',
      latitude: '',
      longitude: '',
    };
    imageList.value = [];
    ruleFormRef.value?.clearValidate();
  };

  const acceptParams = (params: DrawerProps): void => {
    drawerProps.value = params;
    resetForm();
    drawerVisible.value = true;
  };

  const initData = async () => {
    try {
      const districtRes = await getDistrictList();
      districts.value = districtRes.data;

      const foodTypeRes = await getFoodTypeList();
      foodTypes.value = foodTypeRes.data;

      const scenicSpotRes = await getScenicSpotList();
      scenicSpots.value = scenicSpotRes.data;
    } catch (error) {
      console.error('获取数据失败:', error);
    }
  };

  const setType = (type: 'food' | 'scenic') => {
    if (formData.value.type === type) return;
    formData.value.type = type;
    formData.value.foodTypeId = '';
    formData.value.scenicSpotId = '';
  };

  watch(() => formData.value.districtId, () => {
    formData.value.scenicSpotId = '';
  });

  watch(filteredScenicSpots, (options) => {
    if (!options.some((item) => item.id === formData.value.scenicSpotId)) {
      formData.value.scenicSpotId = '';
    }
  });

  const handleImageChange = async (file: any) => {
    if (file.status !== 'ready' || !file.raw) return;
    try {
      imageUploading.value = true;
      const url = await uploadImage(file.raw);
      imageList.value.push(url);
      formData.value.images = imageList.value.join(',');
      ElMessage.success('图片上传成功');
    } catch (error) {
      ElMessage.error('图片上传失败，请重试');
    } finally {
      imageUploading.value = false;
    }
  };

  const removeImage = (index: number) => {
    imageList.value.splice(index, 1);
    formData.value.images = imageList.value.join(',');
  };

  const handleSubmit = () => {
    ruleFormRef.value?.validate(async (valid) => {
      if (!valid) return;
      try {
        loading.value = true;
        const params = clearEmptyValue(formData.value);
        await addCheckin(params);
        ElMessage.success({ message: '发起打卡成功！' });
        await drawerProps.value.getTableList?.();
        drawerVisible.value = false;
      } finally {
        loading.value = false;
      }
    });
  };

  onMounted(() => {
    initData();
  });

  defineExpose({
    acceptParams,
  });
</script>

<style lang="scss" scoped>
  :deep(.el-dialog) {
    margin: 0;
    border-radius: 20px;
    overflow: hidden;
    background: transparent;
    box-shadow: 0 24px 64px rgba(15, 23, 42, 0.18);
  }

  :deep(.el-dialog__header) {
    display: none;
  }

  :deep(.el-dialog__body) {
    padding: 0;
  }

  .drawer-shell {
    display: flex;
    flex-direction: column;
    max-height: min(88vh, 860px);
    background:
      linear-gradient(180deg, #fff8f6 0%, #fff 140px),
      #fff;
  }

  .drawer-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    padding: 24px 24px 18px;
    border-bottom: 1px solid #f3e8e4;

    h2 {
      margin: 6px 0 0;
      font-size: 24px;
      line-height: 1.2;
      color: #111827;
    }

    p {
      margin: 8px 0 0;
      color: #6b7280;
      font-size: 14px;
    }
  }

  .drawer-eyebrow {
    color: #ef4444;
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  .close-btn {
    width: 36px;
    height: 36px;
    border: 0;
    border-radius: 50%;
    color: #6b7280;
    background: #fff;
    box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
    cursor: pointer;
  }

  .drawer-body {
    flex: 1;
    overflow: auto;
    padding: 20px 24px 12px;
  }

  .checkin-form {
    :deep(.el-form-item) {
      margin-bottom: 18px;
    }

    :deep(.el-form-item__label) {
      padding-bottom: 8px;
      color: #374151;
      font-weight: 600;
    }

    :deep(.el-input__wrapper),
    :deep(.el-textarea__inner),
    :deep(.el-select__wrapper) {
      border-radius: 12px;
      box-shadow: none;
      border: 1px solid #e5e7eb;
      background: #fff;
    }

    :deep(.el-textarea__inner) {
      padding-top: 12px;
      line-height: 1.7;
    }
  }

  .hero-section,
  .form-section {
    padding: 18px;
    margin-bottom: 16px;
    border: 1px solid #f1f5f9;
    border-radius: 16px;
    background: #fff;
    box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
  }

  .hero-section {
    background: linear-gradient(180deg, #fff8f6 0%, #fff 100%);
  }

  .hero-copy {
    margin-bottom: 16px;

    h3 {
      margin: 10px 0 0;
      font-size: 18px;
      line-height: 1.4;
    }

    p {
      margin: 8px 0 0;
      color: #6b7280;
      font-size: 13px;
    }
  }

  .hero-badge,
  .cover-chip {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 28px;
    padding: 0 12px;
    border-radius: 999px;
    color: #ef4444;
    background: #fee2e2;
    font-size: 12px;
    font-weight: 700;
  }

  .cover-form-item {
    margin-bottom: 0;
  }

  .cover-upload {
    width: 100%;
  }

  .cover-dropzone {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    width: 100%;
    min-height: 156px;
    border: 1px dashed #f59e8b;
    border-radius: 16px;
    color: #6b7280;
    background: rgba(255, 255, 255, 0.82);
  }

  .cover-icon {
    font-size: 28px;
    color: #ef4444;
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 10px;
    margin-top: 14px;
  }

  .image-card {
    position: relative;

    :deep(.el-image) {
      display: block;
      width: 100%;
      aspect-ratio: 1 / 1;
      border-radius: 12px;
      overflow: hidden;
      background: #f3f4f6;
    }
  }

  .delete-image {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 28px;
    height: 28px;
    border: 0;
    border-radius: 50%;
    color: #fff;
    background: rgba(17, 24, 39, 0.7);
    cursor: pointer;
  }

  .cover-chip {
    position: absolute;
    left: 8px;
    bottom: 8px;
    height: 24px;
    padding: 0 10px;
    font-size: 11px;
    background: rgba(254, 226, 226, 0.95);
  }

  .section-title {
    margin-bottom: 14px;
    color: #111827;
    font-size: 16px;
    font-weight: 700;
  }

  .type-toggle {
    display: inline-flex;
    gap: 10px;
    padding: 4px;
    border-radius: 999px;
    background: #f3f4f6;
  }

  .type-option {
    min-width: 92px;
    height: 38px;
    padding: 0 16px;
    border: 0;
    border-radius: 999px;
    color: #6b7280;
    background: transparent;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .type-option.active {
    color: #fff;
    background: linear-gradient(135deg, #ef4444, #f97316);
    box-shadow: 0 10px 18px rgba(239, 68, 68, 0.24);
  }

  .form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 14px;
  }

  .drawer-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 16px 24px 20px;
    border-top: 1px solid #f3e8e4;
    background: rgba(255, 255, 255, 0.96);
  }

  .footer-tip {
    color: #6b7280;
    font-size: 13px;
  }

  .footer-actions {
    display: flex;
    gap: 10px;
    flex-shrink: 0;

    :deep(.el-button) {
      height: 34px;
      padding: 0 14px;
      border-radius: 999px;
      font-weight: 600;
    }

    :deep(.footer-cancel-btn) {
      color: #6b7280;
      background: #fffafb;
      border: 1px solid #f3d7de;
      box-shadow: none;

      &:hover,
      &:focus {
        color: #ff6d88;
        background: #fff7f8;
        border-color: #f6d9df;
      }
    }

    :deep(.footer-submit-btn) {
      border: 0;
      color: #fff;
      background: linear-gradient(135deg, #ff6d88, #ff8b6e);
      box-shadow: 0 12px 28px rgba(255, 109, 136, 0.22);

      &:hover,
      &:focus {
        color: #fff;
        background: linear-gradient(135deg, #ff6482, #ff8366);
        box-shadow: 0 14px 30px rgba(255, 109, 136, 0.26);
      }
    }
  }

  @media (max-width: 720px) {
    :deep(.el-dialog) {
      width: calc(100vw - 20px) !important;
      max-width: none;
    }

    .drawer-header,
    .drawer-body,
    .drawer-footer {
      padding-left: 16px;
      padding-right: 16px;
    }

    .form-grid,
    .image-grid {
      grid-template-columns: 1fr;
    }

    .drawer-footer {
      flex-direction: column;
      align-items: stretch;
    }

    .footer-actions {
      width: 100%;
    }

    .footer-actions :deep(.el-button) {
      flex: 1;
    }
  }
</style>
