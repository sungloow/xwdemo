const BAIDU_MAP_CALLBACK = '__cityCheckinBaiduMapReady__';

let baiduMapPromise: Promise<any> | null = null;

export const loadBaiduMap = (ak?: string) => {
  if (typeof window === 'undefined') {
    return Promise.reject(new Error('当前环境不支持地图渲染'));
  }

  if ((window as any).BMapGL) {
    return Promise.resolve((window as any).BMapGL);
  }

  if (!ak) {
    return Promise.reject(new Error('请配置 VITE_BAIDU_MAP_AK 后使用地图选点'));
  }

  if (baiduMapPromise) return baiduMapPromise;

  baiduMapPromise = new Promise((resolve, reject) => {
    const existingScript = document.querySelector<HTMLScriptElement>('script[data-baidu-map-gl="true"]');
    const timeout = window.setTimeout(() => {
      reject(new Error('百度地图加载超时，请检查网络或 AK 配置'));
    }, 15000);

    (window as any)[BAIDU_MAP_CALLBACK] = () => {
      window.clearTimeout(timeout);
      resolve((window as any).BMapGL);
    };

    if (existingScript) return;

    const script = document.createElement('script');
    script.dataset.baiduMapGl = 'true';
    script.async = true;
    script.onerror = () => {
      window.clearTimeout(timeout);
      baiduMapPromise = null;
      reject(new Error('百度地图脚本加载失败'));
    };
    script.src = `https://api.map.baidu.com/api?v=1.0&type=webgl&ak=${encodeURIComponent(ak)}&callback=${BAIDU_MAP_CALLBACK}`;
    document.body.appendChild(script);
  });

  return baiduMapPromise;
};
