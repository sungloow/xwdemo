import dayjs from 'dayjs';

// 设置手机号的验证规则
export const phoneRule = (rule: any, value: any, callback: any) => {
  if (rule.required && !value) {
    callback(new Error('请输入联系方式'));
  }
  if (value) {
    const reg = /^1[3|4|5|6|7|8][0-9]\d{8}$/;
    // const phoneReg = /^1[34578]\d{9}$/
    if (reg.test(value)) {
      callback();
    } else {
      return callback(new Error('请输入正确的电话'));
    }
  }
  callback();
};

// 邮箱
export const emailRule = (rule: any, value: any, callback: any) => {
  const mal = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
  if (rule.required && !value) {
    return callback(new Error('邮箱不能为空'));
  }
  if (value) {
    if (!(mal.test(value))) {
      callback(new Error('请输入正确邮箱'));
    } else {
      callback();
    }
  }
  callback();
};

/**
 * 通过身份证号获取出生日期
 * @param idCard
 */
const getBirthdayFromIdCard = (idCard: string) => {
  let birthday = '';
  if (idCard !== null && idCard !== '' && idCard.length === 18) {
    birthday = idCard.substring(6, 10) + '-' + idCard.substring(10, 12) + '-' + idCard.substring(12, 14);
  }
  return birthday;
};

// 校验出生日期
export const birthdayRule = (rule: any, value: any, callback: any, idNo?: string) => {
  const mal = /(^\d{17}(\d|X)$)/;
  if (rule.required && !value) {
    return callback(new Error('不能为空'));
  }
  if (value && idNo) {
    if (!dayjs(value).isSame(getBirthdayFromIdCard(idNo), 'day')) {
      callback(new Error('出生日期与身份证号不匹配'));
    } else {
      callback();
    }
  }
  callback();
};


// 身份证号
export const idCardRule = (rule: any, value: any, callback: any) => {
  const mal = /(^\d{17}(\d|X)$)/;
  if (rule.required && !value) {
    return callback(new Error('不能为空'));
  }
  if (value) {
    if (!(mal.test(value))) {
      callback(new Error('请输入正确身份证号'));
    } else {
      callback();
    }
  }
  callback();
};

// 密码
export const passwordRule = (rule: any, value: any, callback: any) => {
  if (rule.required && !value) {
    return callback(new Error('密码不能为空'));
  }
  // if (value) {
  //   const pattern = /^(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[\x21-\x2f\x3a-\x40\x5b-\x60\x7B-\x7F])[\da-zA-Z\x21-\x2f\x3a-\x40\x5b-\x60\x7B-\x7F]{8,}$/;
  //   if (!(pattern.test(value))) {
  //     callback(new Error('密码强度8位以上，且包含大小写字母、数字、特殊字符'));
  //   } else {
  //     callback();
  //   }
  // }
  callback();
};

// 用户名
export const usernameRule = (rule: any, value: any, callback: any) => {
  if (rule.required && !value) {
    return callback(new Error('用户名不能为空'));
  }
  if (value) {
    if (value.length < 4) {
      callback(new Error('用户名不少于4位'));
    } else if (value.length > 20) {
      callback(new Error('用户名不大于20位'));
    } else {
      callback();
    }
  }
  callback();
};

// 评论说明
export const commentRule = (rule: any, value: any, callback: any) => {
  if (rule.required && !value.trim()) {
    return callback(new Error('说明不能为空'));
  }
  if (value) {
    if (value.trim().length < 6) {
      callback(new Error('说明不少于6个字'));
    } else if (value.trim().length > 100) {
      callback(new Error('说明不大于100个字'));
    } else {
      callback();
    }
  }
  callback();
};

// 菜单管理-权限值格式校验
export const menuCodeRule = (rule: any, value: any, callback: any) => {
  if (rule.required && !value) {
    return callback(new Error('请填写权限值'));
  }
  if (value) {
    // 正则表达式：匹配整个字符串为英文或英文:英文的形式
    const regex = /^[a-zA-Z]+(:[a-zA-Z]+)*$/;
    if (!regex.test(value)) {
      callback(new Error('请填写符合规范的权限值，例：code或者page:childPage'));
    }
    if (value.length > 100) {
      callback(new Error(`长度不能超过100个字符`));
    }
    callback();
  }
  callback();
};

/**
 * 校验文本内容，默认最大长度20个字符
 * @param rule
 * @param value
 * @param callback
 * @param maxSize
 */
export const textRule = (rule: any, value: any, callback: any, maxSize?: number) => {
  if (rule.required && !value) {
    return callback(new Error('字段不能为空'));
  }
  if (value && maxSize) {
    if (value.length > maxSize) {
      callback(new Error(`长度不能超过${maxSize}个字符`));
    } else {
      callback();
    }
  }
  callback();
};
