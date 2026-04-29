<template>
  <div v-if="hasPoint" class="checkin-location-map" :class="{ compact }">
    <div ref="mapContainerRef" class="map-canvas"></div>
    <div v-if="loadError" class="map-error">{{ loadError }}</div>
  </div>
</template>

<script setup lang="ts" name="CheckinLocationMap">
  import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';
  import { loadBaiduMap } from '@/utils/baiduMap';

  const props = defineProps<{
    latitude?: number | string
    longitude?: number | string
    address?: string
    title?: string
    compact?: boolean
  }>();

  const mapContainerRef = ref<HTMLDivElement>();
  const loadError = ref('');
  const hasPoint = computed(() => Number.isFinite(Number(props.latitude)) && Number.isFinite(Number(props.longitude)));

  let BMapGL: any;
  let map: any;
  let marker: any;

  const renderMap = async () => {
    if (!hasPoint.value) return;
    try {
      BMapGL = BMapGL || await loadBaiduMap(import.meta.env.VITE_BAIDU_MAP_AK);
      await nextTick();
      if (!mapContainerRef.value) return;

      const point = new BMapGL.Point(Number(props.longitude), Number(props.latitude));
      if (!map) {
        map = new BMapGL.Map(mapContainerRef.value);
        map.enableDragging();
        map.enableScrollWheelZoom(true);
        map.addControl(new BMapGL.ZoomControl());
      }
      map.centerAndZoom(point, 16);
      if (marker) map.removeOverlay(marker);
      marker = new BMapGL.Marker(point);
      map.addOverlay(marker);

      if (props.address && BMapGL.InfoWindow) {
        const label = props.title ? `${props.title}<br/>${props.address}` : props.address;
        marker.addEventListener('click', () => {
          map.openInfoWindow(new BMapGL.InfoWindow(label), point);
        });
      }
    } catch (error: any) {
      loadError.value = error?.message || '地图加载失败';
    }
  };

  watch(() => [props.latitude, props.longitude, props.address, props.title], renderMap, { immediate: true });

  onBeforeUnmount(() => {
    if (map) map.destroy?.();
  });
</script>

<style scoped lang="scss">
  .checkin-location-map {
    position: relative;
    width: 100%;
    height: 180px;
    margin-top: 12px;
    overflow: hidden;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    background: #eef2f7;
  }

  .checkin-location-map.compact {
    height: 126px;
    margin-top: 0;
  }

  .map-canvas {
    width: 100%;
    height: 100%;
  }

  .map-error {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12px;
    color: #64748b;
    background: #f8fafc;
    font-size: 13px;
  }
</style>
