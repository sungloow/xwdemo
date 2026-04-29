import { createApp } from 'vue';
import pinia from './store/index';
import ElementPlus from 'element-plus';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import directives from '@/directive/index';
import mUI from './components';
import App from './App.vue';
import router from './router';

// element默认主题
import 'element-plus/dist/index.css';
// 公共样式
import '@/styles/index.scss';
// 字体样式
import '@/assets/fonts/font.scss';
// 加载异步路由
import '@/router/initDynamicRouter';

const app = createApp(App);

/** 全局挂载和注册 element-plus 的所有 icon */
app.config.globalProperties.$icons = [];
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.config.globalProperties.$icons.push(key);
  app.component(key, component);
}

app
  .use(ElementPlus, { locale: zhCn })
  .use(mUI)
  .use(router)
  .use(pinia)
  .use(directives)
  .mount('#app');
