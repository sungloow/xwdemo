package com.citycheckin.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private Integer id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String enabled;
    private Integer districtId;
    private String password;
}
