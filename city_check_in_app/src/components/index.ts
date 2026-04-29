import type { App, Component } from 'vue';
import { ProTable } from './ProTable';
import { SearchForm } from './SearchForm';
import PermissionBtn from './btn/index.vue';
import UpdatePassword from './updatePassword/index.vue';
import ClockDial from './clockDial/index.vue';
import ElIconPicker from './elIconPicker/index.vue';


const Components: { [propName: string]: Component } = {
  ProTable, SearchForm, PermissionBtn, UpdatePassword, ClockDial, ElIconPicker
};

export default {
  install: (app: App) => {
    Object.keys(Components).forEach((key) => {
      app.component(key, Components[key]);
    });
  }
};
