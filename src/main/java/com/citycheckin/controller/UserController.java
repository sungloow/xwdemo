package com.citycheckin.controller;

import com.citycheckin.common.PageResult;
import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.dto.AssignRoleDTO;
import com.citycheckin.dto.UserUpdateDTO;
import com.citycheckin.entity.SysUser;
import com.citycheckin.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02-人员管理", description = "用户增删改查、分配角色（超级管理员）")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    public Result<?> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户名（模糊）") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名（模糊）") @RequestParam(required = false) String realName,
            @Parameter(description = "是否启用 1/0") @RequestParam(required = false) String enabled) {
        return Result.ok(PageResult.of(sysUserService.pageUsers(current, size, username, realName, enabled)));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Integer id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        return Result.ok(user);
    }

    @Operation(summary = "新增用户（超级管理员）")
    @PostMapping("/add")
    public Result<?> add(@RequestBody UserUpdateDTO dto) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysUserService.addUser(dto);
        return Result.ok("新增成功");
    }

    @Operation(summary = "编辑用户信息")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UserUpdateDTO dto) {
        sysUserService.updateUser(dto);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除用户（超级管理员）")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysUserService.deleteUser(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "为用户分配角色（超级管理员）")
    @PostMapping("/assignRoles")
    public Result<?> assignRoles(@RequestBody AssignRoleDTO dto) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        sysUserService.assignRoles(dto);
        return Result.ok("分配角色成功");
    }
}
