package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("checkin")
public class Checkin {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer districtId;
    /** food / scenic */
    private String type;
    private Integer foodTypeId;
    private Integer scenicSpotId;
    private String title;
    private String content;
    /** 图片URL，逗号分隔 */
    private String images;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    /** 0-待审核 1-已发布 2-已拒绝 */
    private Integer status;
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 非数据库字段，展示用 */
    @TableField(exist = false)
    private String districtName;
    @TableField(exist = false)
    private String foodTypeName;
    @TableField(exist = false)
    private String scenicSpotName;
    @TableField(exist = false)
    private String username;
}
