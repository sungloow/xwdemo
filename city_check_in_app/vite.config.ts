import { fileURLToPath, URL } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';
// import svgLoader from 'vite-svg-loader';

// https://vitejs.dev/config/
export default defineConfig((config: any) => {
  const { mode } = config;
  const env = loadEnv(mode, process.cwd());
  return {
    base: '/',
    plugins: [
      vue(),
      // * vite 可以使用 jsx/tsx 语法
      vueJsx(),
      // * name 可以写在 script 标签上
      VueSetupExtend(),
      // // * vite-svg-loader支持，可以直接引入svg图标当作组件使用
      // svgLoader()
    ],
    preview: {
      port: 9080,
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          javascriptEnabled: true,
          additionalData: '@import "./src/styles/variable.scss";',
        },
      },
    },
    server: {
      watch: {
          usePolling: true,
          interval: 1000
      },
      host: 'localhost',
      proxy: {
        [env.VITE_BASE_URI]: {
          // 测试环境
          target: 'http://localhost:8080',
          // target: 'http://172.26.2.5:8080',
          // 如果是https接口，需要配置这个参数
          // secure: false,
          // 如果接口跨域，需要进行这个参数配置
          changeOrigin: true,
          rewrite: (path: any) => path.replace(RegExp(`^${env.VITE_BASE_URI}`), '')
        }
      },
    },
  };
});
