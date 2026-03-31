package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scenic_spot")
public class ScenicSpot {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer districtId;
    private String description;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String creator;
    private String updater;
}
