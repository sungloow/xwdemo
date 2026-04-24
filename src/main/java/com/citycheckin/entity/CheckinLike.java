package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("checkin_like")
public class CheckinLike {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer checkinId;
    private Integer userId;
    private LocalDateTime createTime;
}
