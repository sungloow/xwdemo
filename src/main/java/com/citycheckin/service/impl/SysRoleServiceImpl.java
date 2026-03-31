package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.dto.AssignMenuDTO;
import com.citycheckin.entity.SysRoleMenu;
import com.citycheckin.entity.SysUserRole;
import com.citycheckin.mapper.SysRoleMapper;
import com.citycheckin.mapper.SysRoleMenuMapper;
import com.citycheckin.mapper.SysUserRoleMapper;
import com.citycheckin.entity.SysRole;
import com.citycheckin.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public void addRole(SysRole role) {
        role.setEnabled("1");
        role.setCreator(UserContext.getUsername());
        role.setUid(UserContext.getUserId());
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        save(role);
    }

    @Override
    public void updateRole(SysRole role) {
        SysRole existing = getById(role.getId());
        if (existing == null) throw new BusinessException(404, "角色不存在");
        role.setUpdater(UserContext.getUsername());
        role.setUpdateTime(LocalDateTime.now());
        updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Integer id) {
        removeById(id);
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRId, id));
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRId, id));
    }

    @Override
    @Transactional
    public void assignMenus(AssignMenuDTO dto) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRId, dto.getRoleId()));
        if (dto.getMenuIds() != null) {
            dto.getMenuIds().forEach(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRId(dto.getRoleId());
                rm.setMId(menuId);
                sysRoleMenuMapper.insert(rm);
            });
        }
    }

    @Override
    public List<Integer> getMenuIdsByRoleId(Integer roleId) {
        return sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRId, roleId)
        ).stream().map(SysRoleMenu::getMId).collect(Collectors.toList());
    }
}
