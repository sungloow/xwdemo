package com.citycheckin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citycheckin.dto.AssignRoleDTO;
import com.citycheckin.dto.LoginDTO;
import com.citycheckin.dto.RegisterDTO;
import com.citycheckin.dto.ResetPasswordDTO;
import com.citycheckin.dto.UserUpdateDTO;
import com.citycheckin.entity.SysUser;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {

    Map<String, Object> login(LoginDTO dto);

    void register(RegisterDTO dto);

    void logout(String token);

    Page<SysUser> pageUsers(int current, int size, String username, String realName, String enabled);

    void addUser(UserUpdateDTO dto);

    void updateUser(UserUpdateDTO dto);

    void deleteUser(Integer id);

    void assignRoles(AssignRoleDTO dto);

    Map<String, Object> getCurrentUserInfo(Integer userId);

    /** 自助找回密码（用户名 + 手机号验证） */
    void resetPasswordBySelf(ResetPasswordDTO dto);

    /** 管理员重置指定用户密码 */
    void resetPasswordByAdmin(ResetPasswordDTO dto);
}
