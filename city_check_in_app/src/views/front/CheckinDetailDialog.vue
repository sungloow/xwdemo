<template>
  <el-dialog
    ref="dialogRef"
    v-model="visible"
    width="1180px"
    :show-close="false"
    :align-center="true"
    class="checkin-detail-dialog"
    @opened="stripDialogHeader"
    @closed="handleClosed"
  >
    <div class="detail-shell" v-loading="loading">
      <button class="close-btn" type="button" @click="visible = false">
        <el-icon><Close /></el-icon>
      </button>

      <template v-if="detail">
        <div class="detail-media">
          <div class="media-stage" @wheel.prevent.stop="handleMediaWheel">
            <div class="media-stage-meta">
              <span class="media-stage-index">{{ currentImageIndex }}</span>
              <span class="media-stage-count">/ {{ images.length || 1 }}</span>
            </div>
            <button
              v-if="images.length > 1"
              type="button"
              class="media-nav media-nav-prev"
              @click="switchImage(-1)"
            >
              ‹
            </button>
            <button
              v-if="images.length > 1"
              type="button"
              class="media-nav media-nav-next"
              @click="switchImage(1)"
            >
              ›
            </button>
            <img
              class="stage-image"
              :src="currentImage"
              alt=""
              draggable="false"
              @click.stop="openImagePreview"
            />
          </div>
          <div v-if="images.length > 1" class="media-thumbs">
            <button
              v-for="(image, index) in images"
              :key="image"
              type="button"
              :class="{ active: currentImage === image }"
              @click="currentImage = image"
            >
              <img :src="image" alt="" />
              <span class="thumb-order">{{ String(index + 1).padStart(2, '0') }}</span>
            </button>
          </div>
        </div>

        <div class="detail-side">
          <div class="detail-head">
            <div class="author-block">
              <div class="author-avatar">{{ (detail.username || '用').slice(0, 1) }}</div>
              <div>
                <strong>{{ detail.username || '匿名用户' }}</strong>
                <span>{{ formatTime(detail.createTime) }}</span>
              </div>
            </div>
            <div class="detail-tags">
              <span class="detail-tag">{{ detail.type === 'food' ? '美食' : '美景' }}</span>
              <span v-if="detail.districtName" class="detail-tag muted">{{ detail.districtName }}</span>
            </div>
          </div>

          <div class="detail-content">
            <h2>{{ detail.title || '未命名打卡' }}</h2>
            <p v-if="detail.content">{{ detail.content }}</p>
            <div class="detail-meta">
              <span v-if="detail.foodTypeName || detail.scenicSpotName">{{ detail.foodTypeName || detail.scenicSpotName }}</span>
              <span v-if="detail.address">{{ detail.address }}</span>
            </div>
          </div>

          <div class="detail-actions">
            <button
              type="button"
              class="action-btn like-btn"
              :class="{ active: detail.liked }"
              @click="toggleLike"
            >
              <svg class="like-icon" viewBox="0 0 24 24" aria-hidden="true">
                <path
                  class="like-icon-fill"
                  d="M9 10.2 12.9 4c.25-.4.72-.62 1.18-.54 1.33.23 2.1 1.58 1.6 2.83L14.5 9.2h3.4c1.55 0 2.7 1.43 2.36 2.94l-1.05 4.76A3.2 3.2 0 0 1 16.08 19H9V10.2Z"
                />
                <path d="M4 10h3v9H4a1 1 0 0 1-1-1v-7a1 1 0 0 1 1-1Z" />
                <path
                  d="M7 18.8h9.08a3.2 3.2 0 0 0 3.13-2.5l1.05-4.17A2.4 2.4 0 0 0 17.93 9H14.5l1.18-2.71c.5-1.25-.27-2.6-1.6-2.83-.46-.08-.93.14-1.18.54L9 10.2H7"
                />
              </svg>
              <span>{{ detail.liked ? '已点赞' : '赞一下' }}</span>
              <strong>{{ detail.likeCount || 0 }}</strong>
            </button>
            <!-- <button type="button" class="action-btn comment-btn" @click="focusCommentInput">
              <el-icon><ChatDotRound /></el-icon>
              <span>写评论</span>
              <strong>{{ detail.commentCount || 0 }}</strong>
            </button> -->
          </div>

          <div class="comment-wrap">
            <div class="comment-title">
              <div>
                <strong>全部评论</strong>
                <span>{{ detail.commentCount || 0 }} 条互动</span>
              </div>
              <em>按时间顺序展示</em>
            </div>
            <div class="comment-panel">
              <div v-if="comments.length" class="comment-list">
                <div v-for="item in comments" :key="item.id" class="comment-item">
                  <div class="comment-avatar">{{ (item.username || '评').slice(0, 1) }}</div>
                  <div class="comment-body">
                    <div class="comment-line">
                      <strong>{{ item.username || '匿名用户' }}</strong>
                      <span>{{ formatTime(item.createTime) }}</span>
                    </div>
                    <p>{{ item.content }}</p>
                  </div>
                </div>
              </div>
              <el-empty v-else description="还没有评论，来做第一个发言的人吧" :image-size="72" />
            </div>
          </div>

          <div class="comment-editor">
            <el-input
              ref="commentInputRef"
              v-model="commentForm.content"
              type="textarea"
              resize="none"
              :rows="2"
              maxlength="200"
              show-word-limit
              :placeholder="canInteract ? '写下你的评论...' : '登录后才能发表评论'"
              :disabled="!canInteract"
            />
            <div class="editor-footer">
              <span>{{ canInteract ? '说点真诚的、具体的内容更容易收到回应。' : '当前为游客浏览模式。' }}</span>
              <el-button type="primary" :disabled="!canInteract" :loading="commentSubmitting" @click="submitComment">
                发表评论
              </el-button>
            </div>
          </div>
        </div>
      </template>
    </div>
  </el-dialog>
  <el-image-viewer
    v-if="previewVisible"
    :url-list="images.length ? images : [currentImage]"
    :initial-index="currentImagePosition"
    :hide-on-click-modal="true"
    @close="previewVisible = false"
  />
</template>

<script setup lang="ts">
  import { computed, nextTick, ref } from 'vue';
  import { ElMessage } from 'element-plus';
  import type { CheckinComment, CheckinDetail } from '@/apis/checkin';
  import { addCheckinComment, getCheckinComments, getCheckinDetail, likeCheckin, unlikeCheckin } from '@/apis';
  import { ChatDotRound, Close } from '@element-plus/icons-vue';
  import { useUserStore } from '@/store/user';

  const emit = defineEmits<{
    (e: 'changed', payload: { id: number; likeCount: number; commentCount: number; liked: boolean }): void;
  }>();

  const userStore: any = useUserStore();
  const visible = ref(false);
  const loading = ref(false);
  const dialogRef = ref();
  const detail = ref<CheckinDetail | null>(null);
  const comments = ref<CheckinComment[]>([]);
  const currentImage = ref('');
  const previewVisible = ref(false);
  const commentSubmitting = ref(false);
  const commentInputRef = ref();
  const commentForm = ref({ content: '' });

  const canInteract = computed(() => Boolean(userStore.token));
  const images = computed(() => {
    return (detail.value?.images || '').split(',').map((item) => item.trim()).filter(Boolean);
  });
  const currentImageIndex = computed(() => {
    const index = images.value.findIndex((item) => item === currentImage.value);
    return index >= 0 ? String(index + 1).padStart(2, '0') : '01';
  });
  const currentImagePosition = computed(() => {
    const index = images.value.findIndex((item) => item === currentImage.value);
    return index >= 0 ? index : 0;
  });

  const open = async (id: number) => {
    visible.value = true;
    loading.value = true;
    commentForm.value.content = '';
    await stripDialogHeader();
    try {
      const [detailRes, commentsRes]: any = await Promise.all([
        getCheckinDetail(id),
        getCheckinComments(id),
      ]);
      detail.value = detailRes.data;
      comments.value = commentsRes.data || [];
      currentImage.value = images.value[0] || '';
    } finally {
      loading.value = false;
    }
  };

  const refreshDetail = async () => {
    if (!detail.value?.id) return;
    const res: any = await getCheckinDetail(detail.value.id);
    detail.value = res.data;
  };

  const refreshComments = async () => {
    if (!detail.value?.id) return;
    const res: any = await getCheckinComments(detail.value.id);
    comments.value = res.data || [];
  };

  const toggleLike = async () => {
    if (!detail.value) return;
    if (!canInteract.value) {
      ElMessage.warning('请先登录后再点赞');
      return;
    }
    if (detail.value.liked) {
      await unlikeCheckin(detail.value.id);
      detail.value.liked = false;
      detail.value.likeCount = Math.max((detail.value.likeCount || 1) - 1, 0);
      emit('changed', {
        id: detail.value.id,
        likeCount: detail.value.likeCount || 0,
        commentCount: detail.value.commentCount || 0,
        liked: false,
      });
      return;
    }
    await likeCheckin(detail.value.id);
    detail.value.liked = true;
    detail.value.likeCount = (detail.value.likeCount || 0) + 1;
    emit('changed', {
      id: detail.value.id,
      likeCount: detail.value.likeCount || 0,
      commentCount: detail.value.commentCount || 0,
      liked: true,
    });
  };

  const submitComment = async () => {
    if (!detail.value) return;
    if (!canInteract.value) {
      ElMessage.warning('请先登录后再评论');
      return;
    }
    if (!commentForm.value.content.trim()) {
      ElMessage.warning('评论内容不能为空');
      return;
    }
    try {
      commentSubmitting.value = true;
      await addCheckinComment(detail.value.id, { content: commentForm.value.content.trim() });
      commentForm.value.content = '';
      await Promise.all([refreshDetail(), refreshComments()]);
      emit('changed', {
        id: detail.value.id,
        likeCount: detail.value.likeCount || 0,
        commentCount: detail.value.commentCount || 0,
        liked: Boolean(detail.value.liked),
      });
      ElMessage.success('评论成功');
    } finally {
      commentSubmitting.value = false;
    }
  };

  const focusCommentInput = async () => {
    await nextTick();
    commentInputRef.value?.focus?.();
  };

  const stripDialogHeader = async () => {
    await nextTick();
    const dialogEl = dialogRef.value?.dialogContentRef?.$el as HTMLElement | undefined;
    const header = dialogEl?.querySelector('.el-dialog__header') as HTMLElement | null;
    if (!header) return;
    header.classList.remove('el-dialog__header');
    header.setAttribute('hidden', '');
  };

  const switchImage = (step: number) => {
    if (images.value.length <= 1) return;
    const nextIndex = (currentImagePosition.value + step + images.value.length) % images.value.length;
    currentImage.value = images.value[nextIndex];
  };
  const openImagePreview = () => {
    if (!currentImage.value) return;
    previewVisible.value = true;
  };
  let wheelLock = false;
  const handleMediaWheel = (event: WheelEvent) => {
    if (images.value.length <= 1) return;
    if (Math.abs(event.deltaY) < 12 || wheelLock) return;
    wheelLock = true;
    switchImage(event.deltaY > 0 ? 1 : -1);
    setTimeout(() => {
      wheelLock = false;
    }, 180);
  };
  const handleClosed = () => {
    detail.value = null;
    comments.value = [];
    currentImage.value = '';
    previewVisible.value = false;
    commentForm.value.content = '';
    wheelLock = false;
  };

  const formatTime = (value?: string) => {
    if (!value) return '';
    return value.replace('T', ' ').slice(0, 16);
  };

  defineExpose({ open });
</script>

<style scoped lang="scss">
  :deep(.el-dialog) {
    margin: 0;
    max-height: calc(100vh - 20px);
    overflow: hidden;
    border-radius: 24px;
    box-shadow: 0 28px 80px rgba(15, 23, 42, 0.18);
  }

  :deep(.el-dialog__header) {
    display: none;
  }

  :deep(.el-dialog__body) {
    padding: 0;
    overflow: hidden;
  }

  .detail-shell {
    position: relative;
    display: grid;
    grid-template-columns: minmax(0, 1fr) minmax(430px, 0.86fr);
    height: min(760px, calc(100vh - 20px));
    background: #fff;
    overflow: hidden;
    overscroll-behavior: contain;
  }

  .close-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    z-index: 3;
    width: 36px;
    height: 36px;
    border: 0;
    border-radius: 50%;
    color: #374151;
    background: rgba(255, 255, 255, 0.94);
    box-shadow: 0 8px 20px rgba(15, 23, 42, 0.12);
    cursor: pointer;
  }

  .detail-media {
    display: flex;
    flex-direction: column;
    min-width: 0;
    background: linear-gradient(180deg, #fff7f8 0%, #fff1f4 100%);
    overflow: hidden;
    overscroll-behavior: contain;
    touch-action: pan-y;
  }

  .media-stage {
    position: relative;
    flex: 1;
    min-height: 0;
    background: #fff;
    overflow: hidden;
  }

  .media-nav {
    position: absolute;
    top: 50%;
    z-index: 2;
    width: 42px;
    height: 42px;
    border: 0;
    border-radius: 50%;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: rgba(17, 24, 39, 0.42);
    color: #fff;
    font-size: 28px;
    line-height: 1;
    transform: translateY(-50%);
    cursor: pointer;
    backdrop-filter: blur(10px);
    transition: background 0.18s ease, transform 0.18s ease;

    &:hover {
      background: rgba(17, 24, 39, 0.56);
      transform: translateY(-50%) scale(1.04);
    }
  }

  .media-nav-prev {
    left: 18px;
  }

  .media-nav-next {
    right: 18px;
  }

  .stage-image {
    width: 100%;
    height: 100%;
    display: block;
    object-fit: contain;
    object-position: center;
    user-select: none;
    -webkit-user-drag: none;
    cursor: zoom-in;
  }

  .media-stage-meta {
    position: absolute;
    top: 14px;
    left: 14px;
    z-index: 2;
    min-width: 70px;
    height: 36px;
    padding: 0 14px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    background: rgba(17, 24, 39, 0.56);
    backdrop-filter: blur(10px);
    color: #fff;
    box-shadow: 0 12px 30px rgba(17, 24, 39, 0.18);
  }

  .media-stage-index {
    font-size: 15px;
    font-weight: 700;
  }

  .media-stage-count {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
  }

  .media-thumbs {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;
    padding: 12px 16px 14px;
    border-top: 1px solid rgba(255, 109, 136, 0.14);

    button {
      position: relative;
      padding: 0;
      border: 1px solid rgba(255, 109, 136, 0.14);
      border-radius: 16px;
      overflow: hidden;
      background: rgba(255, 255, 255, 0.9);
      box-shadow: 0 10px 24px rgba(255, 109, 136, 0.08);
      cursor: pointer;
      transition: transform 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
    }

    button.active {
      transform: translateY(-2px);
      border-color: #ff5c7a;
      box-shadow: 0 16px 32px rgba(255, 92, 122, 0.2);
    }

    img {
      width: 100%;
      aspect-ratio: 0.95 / 1;
      object-fit: cover;
      display: block;
    }
  }

  .thumb-order {
    position: absolute;
    left: 10px;
    bottom: 10px;
    min-width: 34px;
    height: 24px;
    padding: 0 8px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: rgba(17, 24, 39, 0.62);
    color: #fff;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0;
  }

  .detail-side {
    display: flex;
    flex-direction: column;
    min-width: 0;
    padding: 16px 18px;
    background: linear-gradient(180deg, #ffffff 0%, #fffafb 100%);
    overflow: hidden;
  }

  .detail-head,
  .author-block,
  .detail-tags,
  .detail-actions,
  .comment-item,
  .comment-line,
  .editor-footer {
    display: flex;
    align-items: center;
  }

  .detail-head {
    justify-content: space-between;
    gap: 12px;
    padding-right: 40px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f6e4e8;
  }

  .author-block {
    gap: 10px;
  }

  .author-avatar,
  .comment-avatar {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    background: linear-gradient(135deg, #ff5c7a, #ff8d6b);
    flex-shrink: 0;
  }

  .author-block strong,
  .comment-line strong {
    display: block;
    color: #111827;
  }

  .author-block span,
  .comment-line span {
    color: #9ca3af;
    font-size: 12px;
  }

  .detail-tags {
    gap: 8px;
    flex-wrap: wrap;
  }

  .detail-tag {
    height: 24px;
    padding: 0 10px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    background: #fff1f4;
    color: #ff5c7a;
    font-size: 12px;
    font-weight: 700;
  }

  .detail-tag.muted {
    background: #f8fafc;
    color: #6b7280;
  }

  .detail-content {
    flex-shrink: 0;
    max-height: 168px;
    padding: 12px 0 10px;
    overflow: auto;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #f2c8d1;
      border-radius: 999px;
    }

    h2 {
      margin: 0;
      color: #111827;
      font-size: 22px;
      line-height: 1.32;
    }

    p {
      margin: 10px 0 0;
      color: #374151;
      line-height: 1.7;
      white-space: pre-wrap;
    }
  }

  .detail-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 10px;
    color: #6b7280;
    font-size: 13px;
  }

  .detail-actions {
    gap: 8px;
    padding: 6px 0 8px;
    border-bottom: 1px solid #f6e4e8;
  }

  .action-btn {
    height: 32px;
    padding: 0 10px;
    border: 1px solid #f6d9df;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: #fff7f8;
    color: #4b5563;
    cursor: pointer;
    transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, background 0.18s ease;

    :deep(.el-icon) {
      font-size: 13px;
    }

    .like-icon {
      width: 14px;
      height: 14px;
      flex-shrink: 0;
      fill: none;
      stroke: currentColor;
      stroke-width: 1.8;
      stroke-linecap: round;
      stroke-linejoin: round;
    }

    .like-icon-fill {
      fill: currentColor;
      stroke: none;
      opacity: 0;
      transition: opacity 0.18s ease;
    }

    span {
      font-size: 12px;
    }

    strong {
      color: #111827;
      font-size: 12px;
      font-weight: 700;
    }

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 12px 28px rgba(255, 109, 136, 0.1);
    }
  }

  .action-btn.active {
    color: #fff;
    background: linear-gradient(135deg, #ff6d88, #ff8b6e);
    border-color: transparent;
    box-shadow: 0 16px 34px rgba(255, 109, 136, 0.24);

    strong {
      color: #fff;
    }

    .like-icon-fill {
      opacity: 0.28;
    }
  }

  .like-btn {
    padding-right: 11px;
  }

  .comment-btn {
    background: #fff;
  }

  .comment-wrap {
    flex: 1;
    min-height: 0;
    padding-top: 10px;
    display: flex;
    flex-direction: column;
  }

  .comment-title {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;

    strong {
      display: block;
      color: #111827;
      font-size: 15px;
      font-weight: 700;
    }

    span,
    em {
      color: #9ca3af;
      font-size: 12px;
      font-style: normal;
    }
  }

  .comment-panel {
    min-height: 0;
    flex: 1;
    padding: 4px 6px 4px 0;
    border-radius: 16px;
    background: linear-gradient(180deg, #fff8f9 0%, #fffdfd 100%);
    border: 1px solid #f8e7eb;
    overflow: hidden;
  }

  .comment-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    height: 100%;
    overflow: auto;
    padding: 6px 8px 6px 14px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: #f2c8d1;
      border-radius: 999px;
    }
  }

  .comment-item {
    align-items: flex-start;
    gap: 8px;
    padding: 9px 10px 9px 0;
    border-bottom: 1px solid rgba(246, 228, 232, 0.9);

    &:last-child {
      border-bottom: 0;
    }
  }

  .comment-avatar {
    width: 30px;
    height: 30px;
    font-size: 12px;
    box-shadow: 0 8px 18px rgba(255, 109, 136, 0.16);
  }

  .comment-body {
    flex: 1;
    min-width: 0;

    p {
      margin: 4px 0 0;
      color: #374151;
      line-height: 1.6;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }

  .comment-line {
    justify-content: space-between;
    gap: 10px;
  }

  .comment-editor {
    flex-shrink: 0;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #f6e4e8;

    :deep(.el-textarea__inner) {
      border-radius: 14px;
      padding: 10px 12px;
      background: #fffafb;
      border-color: #f3d7de;
      box-shadow: none;
    }
  }

  .editor-footer {
    justify-content: space-between;
    gap: 12px;
    margin-top: 8px;

    span {
      color: #9ca3af;
      font-size: 12px;
    }

    :deep(.el-button) {
      height: 34px;
      padding: 0 14px;
      border: 0;
      border-radius: 999px;
      background: linear-gradient(135deg, #ff6d88, #ff8b6e);
      box-shadow: 0 12px 28px rgba(255, 109, 136, 0.22);
    }
  }

  @media (max-width: 1100px) {
    :deep(.el-dialog) {
      width: calc(100vw - 20px) !important;
      max-height: calc(100vh - 20px);
    }

    .detail-shell {
      grid-template-columns: 1fr;
      height: min(860px, calc(100vh - 20px));
    }

    .media-stage {
      min-height: 280px;
    }
  }

  @media (max-width: 640px) {
    .detail-side {
      padding: 20px 16px 16px;
    }

    .detail-head {
      padding-right: 44px;
    }

    .media-thumbs {
      grid-template-columns: repeat(3, minmax(0, 1fr));
      padding: 12px 14px 14px;
    }

    .detail-actions {
      flex-wrap: wrap;
    }

    .comment-title,
    .editor-footer {
      align-items: flex-start;
      flex-direction: column;
    }
  }
</style>
