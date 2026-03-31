package com.citycheckin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer pid;
    private String type;
    private Integer level;
    private String code;
    private String name;
    private String componentName;
    private String icon;
    @TableField("IS_SHOW")
    private Integer isShow;
    private String component;
    private String path;
    @TableField("SORT_ORDER")
    private Integer sortOrder;
    private Integer affix;
    private Integer keepAlive;
    private String activeMenu;
    private String redirect;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String creator;
    private String updater;

    /** 子菜单，不映射数据库字段 */
    @TableField(exist = false)
    private List<SysMenu> children;
}
