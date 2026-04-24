package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("checkin_comment")
public class CheckinComment {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer checkinId;
    private Integer userId;
    private String content;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String username;
}
