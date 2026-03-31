package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.common.TokenManager;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.dto.AssignRoleDTO;
import com.citycheckin.dto.LoginDTO;
import com.citycheckin.dto.RegisterDTO;
import com.citycheckin.dto.ResetPasswordDTO;
import com.citycheckin.dto.UserUpdateDTO;
import com.citycheckin.entity.SysRole;
import com.citycheckin.entity.SysUser;
import com.citycheckin.entity.SysUserRole;
import com.citycheckin.entity.SysMenu;
import com.citycheckin.mapper.SysMenuMapper;
import com.citycheckin.mapper.SysRoleMapper;
import com.citycheckin.service.SysMenuService;
import com.citycheckin.mapper.SysUserMapper;
import com.citycheckin.mapper.SysUserRoleMapper;
import com.citycheckin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final TokenManager tokenManager;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysMenuService sysMenuService;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(400, "用户名不存在");
        }
        if (!"1".equals(user.getEnabled())) {
            throw new BusinessException(403, "账号已被禁用");
        }
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(400, "密码错误");
        }
        String token = tokenManager.createToken(user);
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("isSuper", user.getIsSuper());
        result.put("districtId", user.getDistrictId());
        result.put("roles", roles.stream().map(SysRole::getCode).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void register(RegisterDTO dto) {
        long count = count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setEnabled("1");
        user.setIsSuper(0);
        user.setSalt("salt");
        user.setCreator("register");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);

        // 默认分配普通用户角色（ID=3）
        SysUserRole userRole = new SysUserRole();
        userRole.setUId(user.getId());
        userRole.setRId(3);
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public void logout(String token) {
        tokenManager.removeToken(token);
    }

    @Override
    public Page<SysUser> pageUsers(int current, int size, String username, String realName, String enabled) {
        Page<SysUser> page = new Page<>(current, size);
        return baseMapper.pageUsers(page, username, realName, enabled);
    }

    @Override
    public void addUser(UserUpdateDTO dto) {
        long count = count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername() != null ? dto.getUsername() : ""));
        if (count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : "1");
        user.setDistrictId(dto.getDistrictId());
        user.setPassword(dto.getPassword() != null ? dto.getPassword() : "123456");
        user.setIsSuper(0);
        user.setSalt("salt");
        user.setUid(UserContext.getUserId());
        user.setCreator(UserContext.getUsername());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);
    }

    @Override
    public void updateUser(UserUpdateDTO dto) {
        SysUser user = getById(dto.getId());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (dto.getRealName() != null) user.setRealName(dto.getRealName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getEnabled() != null) user.setEnabled(dto.getEnabled());
        if (dto.getDistrictId() != null) user.setDistrictId(dto.getDistrictId());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) user.setPassword(dto.getPassword());
        user.setUpdater(UserContext.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public void deleteUser(Integer id) {
        if (id.equals(UserContext.getUserId())) {
            throw new BusinessException(400, "不能删除自己");
        }
        removeById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUId, id));
    }

    @Override
    public List<SysRole> getUserRoles(Integer userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional
    public void removeUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        Integer currentUserId = UserContext.getUserId();
        if (ids.contains(currentUserId)) {
            throw new BusinessException(400, "不能删除自己");
        }
        removeByIds(ids);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUId, ids));
    }

    @Override
    @Transactional
    public void assignRoles(AssignRoleDTO dto) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUId, dto.getUserId()));
        if (dto.getRoleIds() != null) {
            dto.getRoleIds().forEach(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUId(dto.getUserId());
                ur.setRId(roleId);
                sysUserRoleMapper.insert(ur);
            });
        }
    }

    @Override
    public void resetPasswordBySelf(ResetPasswordDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (dto.getPhone() == null || dto.getPhone().isEmpty()) {
            throw new BusinessException(400, "手机号不能为空");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new BusinessException(400, "新密码不能少于6位");
        }
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(400, "用户名不存在");
        }
        if (!dto.getPhone().equals(user.getPhone())) {
            throw new BusinessException(400, "手机号与账号不匹配，验证失败");
        }
        if (!"1".equals(user.getEnabled())) {
            throw new BusinessException(403, "账号已被禁用，无法重置密码");
        }
        user.setPassword(dto.getNewPassword());
        user.setUpdater(user.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public void resetPasswordByAdmin(ResetPasswordDTO dto) {
        if (dto.getUserId() == null) {
            throw new BusinessException(400, "userId 不能为空");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new BusinessException(400, "新密码不能少于6位");
        }
        SysUser user = getById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setPassword(dto.getNewPassword());
        user.setUpdater(UserContext.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }

    @Override
    public Map<String, Object> getCurrentUserInfo(Integer userId) {
        SysUser user = getById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(userId);

        // 平铺列表用于提取按钮 code
        List<SysMenu> flatMenus = Integer.valueOf(1).equals(user.getIsSuper())
                ? sysMenuMapper.selectList(null)
                : sysMenuMapper.selectMenusByUserId(userId);

        // 带 children 的完整菜单树（非按钮类型，含所有字段）
        List<SysMenu> menuTree = sysMenuService.getMenuTreeByUserId(userId);

        List<SysMenu> buttonCodes = flatMenus.stream()
                .filter(m -> "button".equals(m.getType()) && m.getCode() != null && !m.getCode().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("realName", user.getRealName());
        info.put("email", user.getEmail());
        info.put("phone", user.getPhone());
        info.put("isSuper", user.getIsSuper());
        info.put("districtId", user.getDistrictId());
        info.put("roles", roles.stream().map(SysRole::getCode).collect(Collectors.toList()));
        info.put("menus", menuTree);
        info.put("buttons", buttonCodes);
        return info;
    }
}
