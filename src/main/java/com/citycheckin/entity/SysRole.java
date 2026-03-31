package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String roleName;
    private String roleType;
    private String description;
    private String enabled;
    private Integer uid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String updater;
    private String creator;
}
