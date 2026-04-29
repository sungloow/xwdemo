/**
 * 判断一个数组是否包含另一个数组
 * @param parentArr
 * @param childrenArr
 */
export const isArrIncludes = (parentArr: any[] = [], childrenArr: any [] = []) => {
  return childrenArr.every((v: any) => {
    return parentArr.includes(v);
  });
};

/**
 * 判断一个数组是否等于另一个数组
 * @param parentArr
 * @param childrenArr
 */
export const isArrayEqual = (parentArr: any[] = [], childrenArr: any [] = []) => {
  return parentArr?.length === childrenArr?.length && isArrIncludes(parentArr, childrenArr);
};

/**
 * 判断两个数组是否存在交集
 * @param parentArr
 * @param childrenArr
 */
export const isArrayIntersection = (parentArr: any[] = [], childrenArr: any [] = []) => {
  return parentArr.concat(childrenArr).length !==
    Array.from(new Set([...parentArr, ...childrenArr])).length;
};

/**
 * 数组转成树结构数据
 * @param parentArr
 * @param parentId 父节点Id
 */
export const arrayToTreeData = (parentArr: any[], parentId: number,) => {
  const tree = (id: number) => {
    const arr: any = [];
    const temp = parentArr.filter((item:any) => {
      return item.parentId === id;
    });
    temp.forEach((item: any) => {
      arr.push({
        ...item,
        title: item.title,
        value: item.value,
        children: tree(item.id),
      });
    });
    return arr;
  };
  return tree(parentId);
};


/**
 * 多个数组的交集
 * @param arrs 二维数组
 */
export const intersectNoRepeatTwice = (arrs:any) => {

  return arrs.reduce(function (prev: any, cur: any) {

    // return [...new Set(cur.filter((item)=>prev.includes(item)))]

    return Array.from(new Set(cur.filter((item:any) => prev.includes(item))));

  });

};
