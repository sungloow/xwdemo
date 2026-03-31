package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.dto.AssignMenuDTO;
import com.citycheckin.entity.SysRole;
import com.citycheckin.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "03-角色管理", description = "角色增删改查、分配菜单权限（超级管理员）")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(sysRoleService.list());
    }

    @Operation(summary = "角色详情（含已分配菜单ID列表）")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) throw new BusinessException(404, "角色不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("role", role);
        data.put("menuIds", sysRoleService.getMenuIdsByRoleId(id));
        return Result.ok(data);
    }

    @Operation(summary = "新增角色（超级管理员）")
    @PostMapping("/add")
    public Result<?> add(@RequestBody SysRole role) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysRoleService.addRole(role);
        return Result.ok("新增成功");
    }

    @Operation(summary = "编辑角色（超级管理员）")
    @PutMapping("/update")
    public Result<?> update(@RequestBody SysRole role) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysRoleService.updateRole(role);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除角色（超级管理员）", description = "同时清除该角色下的用户关联和菜单关联")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysRoleService.deleteRole(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "为角色分配菜单（超级管理员）")
    @PostMapping("/assignMenus")
    public Result<?> assignMenus(@RequestBody AssignMenuDTO dto) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysRoleService.assignMenus(dto);
        return Result.ok("菜单分配成功");
    }
}
