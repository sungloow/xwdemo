package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.entity.SysMenu;
import com.citycheckin.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "04-菜单管理", description = "菜单树查询与维护（超级管理员）")
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "获取完整菜单树", description = "超级管理员用，包含所有菜单节点")
    @GetMapping("/tree")
    public Result<?> tree() {
        return Result.ok(sysMenuService.getMenuTree());
    }

    @Operation(summary = "获取当前用户可见菜单树", description = "根据登录用户角色返回有权限的菜单")
    @GetMapping("/user")
    public Result<?> userMenus() {
        return Result.ok(sysMenuService.getMenuTreeByUserId(UserContext.getUserId()));
    }

    @Operation(summary = "新增菜单（超级管理员）")
    @PostMapping("/add")
    public Result<?> add(@RequestBody SysMenu menu) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysMenuService.addMenu(menu);
        return Result.ok("新增成功");
    }

    @Operation(summary = "编辑菜单（超级管理员）")
    @PutMapping("/update")
    public Result<?> update(@RequestBody SysMenu menu) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysMenuService.updateMenu(menu);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除菜单（超级管理员）", description = "递归删除子菜单，并同步清理角色-菜单关联")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysMenuService.deleteMenu(id);
        return Result.ok("删除成功");
    }
}
