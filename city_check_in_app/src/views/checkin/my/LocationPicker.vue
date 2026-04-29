<template>
  <div class="location-picker">
    <div class="picker-toolbar">
      <el-input
        v-model="keyword"
        maxlength="80"
        placeholder="搜索地点，例如：解放碑、洪崖洞、观音桥"
        clearable
        @input="handleKeywordInput"
        @clear="clearSearchResults"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :loading="searching" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div v-if="loadError" class="map-placeholder">
      <el-icon><LocationInformation /></el-icon>
      <span>{{ loadError }}</span>
    </div>

    <div v-show="!loadError" class="map-layout">
      <div ref="mapContainerRef" class="map-canvas"></div>

      <aside class="result-panel">
        <div v-if="selectedAddress" class="selected-place">
          <span class="panel-label">已选位置</span>
          <strong>{{ selectedName || selectedAddress }}</strong>
          <p>{{ selectedAddress }}</p>
          <small v-if="selectedPoint">{{ selectedPoint.latitude }}, {{ selectedPoint.longitude }}</small>
        </div>

        <div v-if="searchResults.length" class="result-list">
          <button
            v-for="item in searchResults"
            :key="`${item.title}-${item.address}-${item.latitude}-${item.longitude}`"
            class="result-item"
            type="button"
            @click="selectSearchResult(item)"
          >
            <strong>{{ item.title }}</strong>
            <span>{{ item.address || '暂无详细地址' }}</span>
          </button>
        </div>

        <el-empty
          v-else
          :image-size="72"
          description="点击地图或搜索地点"
        />
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts" name="LocationPicker">
  import { nextTick, onBeforeUnmount, ref, watch } from 'vue';
  import { ElMessage } from 'element-plus';
  import { LocationInformation } from '@element-plus/icons-vue';
  import { loadBaiduMap } from '@/utils/baiduMap';

  interface LocationValue {
    address?: string
    latitude?: number | string
    longitude?: number | string
    province?: string
    city?: string
    district?: string
  }

  interface SearchResult {
    title: string
    address: string
    latitude: number
    longitude: number
    point: any
  }

  const props = defineProps<{
    modelValue?: LocationValue
  }>();

  const emit = defineEmits<{
    (e: 'update:modelValue', value: LocationValue): void
    (e: 'change', value: LocationValue): void
  }>();

  const mapContainerRef = ref<HTMLDivElement>();
  const keyword = ref('');
  const searching = ref(false);
  const loadError = ref('');
  const searchResults = ref<SearchResult[]>([]);
  const selectedAddress = ref('');
  const selectedName = ref('');
  const selectedPoint = ref<LocationValue | null>(null);

  let BMapGL: any;
  let map: any;
  let marker: any;
  let geocoder: any;
  let localSearch: any;
  let suggestTimer: number | undefined;

  const isValidLngLat = (lng: unknown, lat: unknown) => {
    const lngNumber = Number(lng);
    const latNumber = Number(lat);
    return Number.isFinite(lngNumber)
      && Number.isFinite(latNumber)
      && lngNumber >= -180
      && lngNumber <= 180
      && latNumber >= -90
      && latNumber <= 90;
  };

  const createPoint = (lng: unknown, lat: unknown) => {
    if (!isValidLngLat(lng, lat)) return null;
    return new BMapGL.Point(Number(lng), Number(lat));
  };

  const getEventPoint = (event: any) => {
    const rawPoint = event?.latlng || event?.point;
    return createPoint(rawPoint?.lng, rawPoint?.lat);
  };

  const toFixedPoint = (point: any): LocationValue => ({
    latitude: Number(point.lat.toFixed(6)),
    longitude: Number(point.lng.toFixed(6)),
  });

  const syncSelection = (point: any, address: string, name = '', region: Partial<LocationValue> = {}) => {
    if (!map || !point) return;

    if (marker) {
      map.removeOverlay(marker);
    }
    marker = new BMapGL.Marker(point);
    map.addOverlay(marker);
    map.panTo(point);

    const pointValue = toFixedPoint(point);
    const value = {
      address,
      latitude: pointValue.latitude,
      longitude: pointValue.longitude,
      ...region,
    };
    selectedAddress.value = address;
    selectedName.value = name;
    selectedPoint.value = pointValue;
    emit('update:modelValue', value);
    emit('change', value);
  };

  const getAddressFromPoint = (point: any, name = '') => {
    if (!geocoder) return;
    geocoder.getLocation(point, (result: any) => {
      const addressComponents = result?.addressComponents || {};
      const province = addressComponents.province || '';
      const city = addressComponents.city || province;
      const district = addressComponents.district || '';
      const isChongqing = province === '重庆市' || city === '重庆市';
      if (!isChongqing) {
        ElMessage.warning('只能选择重庆市以内的地点');
        return;
      }

      const address = result?.address || [
        province,
        city,
        district,
        addressComponents.street,
        addressComponents.streetNumber,
      ].filter(Boolean).join('');
      syncSelection(point, address || `${point.lat}, ${point.lng}`, name, {
        province,
        city,
        district,
      });
    });
  };

  const selectSearchResult = (item: SearchResult) => {
    map.centerAndZoom(item.point, 17);
    getAddressFromPoint(item.point, item.title);
    keyword.value = item.title;
  };

  const searchByGeocoder = () => {
    geocoder.getPoint(keyword.value, (point: any) => {
      searching.value = false;
      if (!point || !isValidLngLat(point.lng, point.lat)) {
        ElMessage.warning('没有找到相关地点');
        return;
      }
      map.centerAndZoom(point, 17);
      getAddressFromPoint(point, keyword.value);
    }, '重庆市');
  };

  const runLocalSearch = (text: string, autoFocus = false) => {
    if (!text || !map || !BMapGL) return;
    if (!BMapGL.LocalSearch) {
      searchByGeocoder();
      return;
    }

    searching.value = autoFocus;
    localSearch = new BMapGL.LocalSearch(map, {
      pageCapacity: 8,
      onSearchComplete: (results: any) => {
        searching.value = false;
        if (!autoFocus && text !== keyword.value.trim()) return;

        const pois: SearchResult[] = [];
        const count = results?.getCurrentNumPois?.() || 0;
        for (let index = 0; index < count; index += 1) {
          const poi = results.getPoi(index);
          if (!poi?.point || !isValidLngLat(poi.point.lng, poi.point.lat)) continue;
          pois.push({
            title: poi.title || text,
            address: poi.address || '',
            latitude: Number(poi.point.lat.toFixed(6)),
            longitude: Number(poi.point.lng.toFixed(6)),
            point: poi.point,
          });
        }
        searchResults.value = pois;
        if (autoFocus && pois[0]) {
          map.centerAndZoom(pois[0].point, 16);
        } else if (autoFocus) {
          searchByGeocoder();
        }
      },
    });
    localSearch.setLocation?.('重庆市');
    localSearch.search(text);
  };

  const handleKeywordInput = () => {
    window.clearTimeout(suggestTimer);
    const text = keyword.value.trim();
    if (text.length < 2) {
      searchResults.value = [];
      return;
    }
    suggestTimer = window.setTimeout(() => {
      runLocalSearch(text, false);
    }, 350);
  };

  const clearSearchResults = () => {
    window.clearTimeout(suggestTimer);
    searchResults.value = [];
  };

  const handleSearch = () => {
    const text = keyword.value.trim();
    if (!text || !map || !BMapGL) return;

    keyword.value = text;
    searchResults.value = [];
    runLocalSearch(text, true);
  };

  const initMap = async () => {
    try {
      BMapGL = await loadBaiduMap(import.meta.env.VITE_BAIDU_MAP_AK);
      await nextTick();
      if (!mapContainerRef.value) return;

      map = new BMapGL.Map(mapContainerRef.value);
      const center = new BMapGL.Point(106.551556, 29.563009);
      map.centerAndZoom(center, 12);
      map.enableDragging();
      map.enableScrollWheelZoom(true);
      map.enableInertialDragging?.();
      map.disableDoubleClickZoom?.();
      map.addControl(new BMapGL.ZoomControl());
      if (BMapGL.ScaleControl) {
        map.addControl(new BMapGL.ScaleControl());
      }
      geocoder = new BMapGL.Geocoder();
      map.addEventListener('click', (event: any) => {
        const point = getEventPoint(event);
        if (!point) return;
        getAddressFromPoint(point);
      });

      if (props.modelValue?.latitude && props.modelValue?.longitude) {
        const point = createPoint(props.modelValue.longitude, props.modelValue.latitude);
        if (point) {
          syncSelection(point, props.modelValue.address || '', '');
          map.centerAndZoom(point, 16);
        }
      }
    } catch (error: any) {
      loadError.value = error?.message || '地图加载失败';
    }
  };

  watch(() => props.modelValue?.address, (address) => {
    if (!address || address === selectedAddress.value) return;
    selectedAddress.value = address;
  });

  onBeforeUnmount(() => {
    window.clearTimeout(suggestTimer);
    if (map) {
      map.destroy?.();
    }
  });

  initMap();
</script>

<style lang="scss" scoped>
  .location-picker {
    width: 100%;
    overflow: hidden;
    border: 1px solid #e5e7eb;
    border-radius: 14px;
    background: #fff;
  }

  .picker-toolbar {
    padding: 12px;
    border-bottom: 1px solid #eef2f7;
    background: #f8fafc;
  }

  .map-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 260px;
    min-height: 440px;
  }

  .map-canvas {
    min-height: 440px;
    background: #eef2f7;
  }

  .result-panel {
    display: flex;
    flex-direction: column;
    gap: 10px;
    max-height: 440px;
    overflow: auto;
    padding: 12px;
    border-left: 1px solid #eef2f7;
    background: #fff;
  }

  .panel-label {
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }

  .selected-place {
    padding: 10px;
    border: 1px solid #dbeafe;
    border-radius: 10px;
    background: #eff6ff;

    strong {
      display: block;
      margin-top: 6px;
      color: #172554;
      line-height: 1.4;
    }

    p {
      margin: 6px 0 0;
      color: #475569;
      font-size: 12px;
      line-height: 1.5;
    }

    small {
      display: block;
      margin-top: 6px;
      color: #64748b;
    }
  }

  .result-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .result-item {
    width: 100%;
    padding: 9px 10px;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    text-align: left;
    background: #fff;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      border-color: #93c5fd;
      background: #f8fafc;
    }

    strong,
    span {
      display: block;
    }

    strong {
      color: #111827;
      line-height: 1.4;
    }

    span {
      margin-top: 4px;
      color: #64748b;
      font-size: 12px;
      line-height: 1.5;
    }
  }

  .map-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    min-height: 160px;
    padding: 20px;
    color: #64748b;
    background: #f8fafc;
  }

  @media (max-width: 720px) {
    .map-layout {
      grid-template-columns: 1fr;
    }

    .result-panel {
      max-height: none;
      border-left: 0;
      border-top: 1px solid #eef2f7;
    }
  }
</style>
