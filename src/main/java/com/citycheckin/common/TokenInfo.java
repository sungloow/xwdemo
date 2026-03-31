package com.citycheckin.common;

import lombok.Data;

@Data
public class TokenInfo {
    private Integer userId;
    private String username;
    private Integer isSuper;
    private Integer districtId;
    private Long expireTime;
}
