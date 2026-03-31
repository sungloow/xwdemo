package com.citycheckin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citycheckin.dto.AssignMenuDTO;
import com.citycheckin.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    void addRole(SysRole role);

    void updateRole(SysRole role);

    void deleteRole(Integer id);

    void assignMenus(AssignMenuDTO dto);

    List<Integer> getMenuIdsByRoleId(Integer roleId);
}
