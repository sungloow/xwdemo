// 初始化dialog标题
export const getDialogTitle = (params: any): string => {
  const level = params.rowData && Number(params.rowData.level);
  const isAddType = params.apiType === 1;
  switch (level) {
    case 1:
      return isAddType? '添加二级菜单':'修改一级菜单';
    case 2:
      return isAddType? '添加功能':'修改二级菜单';
    case 3:
      return '修改功能';
    default:
      return '添加一级菜单';
  }
};
