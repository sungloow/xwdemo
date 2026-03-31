package com.citycheckin.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    /** 用户名（自助找回时必填） */
    private String username;
    /** 手机号（自助找回时必填，用于身份核验） */
    private String phone;
    /** 新密码 */
    private String newPassword;
    /** 管理员重置时直接传 userId，不需要 username/phone */
    private Integer userId;
}
