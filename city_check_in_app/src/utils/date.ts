import dayjs from 'dayjs';


//表格时间格式化
export const tableDateFormat = (orgDate: any, format?: string) => {
  return orgDate ? dayjs(orgDate).format(format ?? 'YYYY-MM-DD HH:mm') : '--';
};

/**
 * 秒转换成 分-秒
 * @param value
 */
export const secondFormat = (value: any = 0) => {
  // 秒
  let theTime: number = parseInt(value);
  // 分
  let middle: number = 0;
  // 小时
  let hour: number = 0;
  if (theTime >= 60) {
    // @ts-ignore
    middle = parseInt(theTime / 60);
    // @ts-ignore
    theTime = parseInt(theTime % 60);
    if (middle >= 60) {
      // @ts-ignore
      hour = parseInt(middle / 60);
      // @ts-ignore
      middle = parseInt(middle % 60);
    }
  }
  // @ts-ignore
  let result = "" + parseInt(theTime) + "秒";
  if (middle > 0) {
    // @ts-ignore
    result = "" + parseInt(middle) + "分" + result;
  }
  if (hour > 0) {
    // @ts-ignore
    result = "" + parseInt(hour) + "小时" + result;
  }
  return result;
};

export function timeFix() {
  const time = new Date();
  const hour = time.getHours();
  return hour < 9
    ? '早上好'
    : hour <= 11
      ? '上午好'
      : hour <= 13
        ? '中午好'
        : hour < 20
          ? '下午好'
          : '晚上好';
}
