/**
 * 清除对象中值为空的key
 * @param obj
 */
export const clearEmptyValue = (obj: any) => {
  Object.keys(obj).forEach(key => {
    if (obj[key] === null || obj[key] === '' || (Array.isArray(obj[key]) && obj[key].length === 0)) {
      delete obj[key];
    }
  });
  return obj;
};
