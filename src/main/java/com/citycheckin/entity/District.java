package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("district")
public class District {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
