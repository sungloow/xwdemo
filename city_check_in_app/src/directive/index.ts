import permission from './permission';

const directivesList: any = {
  permission,
};

const directives = {
  install: (app: any) => {
    Object.keys(directivesList).forEach((key) => {
      // 注册所有自定义指令
      app.directive(key, directivesList[key])
    })
  }
};

export default directives;
