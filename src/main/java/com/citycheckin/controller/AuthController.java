package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.dto.LoginDTO;
import com.citycheckin.dto.RegisterDTO;
import com.citycheckin.dto.ResetPasswordDTO;
import com.citycheckin.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "01-认证管理", description = "登录 / 注册 / 退出 / 当前用户信息")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录", description = "返回 token，后续请求携带 Authorization: Bearer {token}")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO dto) {
        return Result.ok(sysUserService.login(dto));
    }

    @Operation(summary = "用户注册", description = "注册后默认分配普通用户角色")
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        sysUserService.register(dto);
        return Result.ok("注册成功");
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        sysUserService.logout(token);
        return Result.ok("退出成功");
    }

    @Operation(summary = "获取当前登录用户信息（含角色）")
    @GetMapping("/info")
    public Result<?> info() {
        return Result.ok(sysUserService.getCurrentUserInfo(UserContext.getUserId()));
    }

    @Operation(summary = "忘记密码（自助重置）",
            description = "无需登录。通过「用户名 + 注册手机号」验证身份后设置新密码，新密码不能少于6位")
    @PostMapping("/forgotPassword")
    public Result<?> forgotPassword(@RequestBody ResetPasswordDTO dto) {
        sysUserService.resetPasswordBySelf(dto);
        return Result.ok("密码重置成功，请使用新密码登录");
    }

    @Operation(summary = "管理员重置用户密码",
            description = "需要登录且为超级管理员。传入 userId 和 newPassword 直接重置，无需原密码验证")
    @PostMapping("/resetPassword")
    public Result<?> resetPassword(@RequestBody ResetPasswordDTO dto) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "仅超级管理员可执行此操作");
        sysUserService.resetPasswordByAdmin(dto);
        return Result.ok("密码重置成功");
    }
}
