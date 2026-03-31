package com.citycheckin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citycheckin.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT u.* FROM sys_user u WHERE u.ID = #{userId}")
    SysUser selectById(@Param("userId") Integer userId);

    @Select("<script>" +
            "SELECT u.* FROM sys_user u" +
            "<where>" +
            "<if test='username != null and username != \"\"'> AND u.USERNAME LIKE CONCAT('%',#{username},'%')</if>" +
            "<if test='realName != null and realName != \"\"'> AND u.REAL_NAME LIKE CONCAT('%',#{realName},'%')</if>" +
            "<if test='enabled != null and enabled != \"\"'> AND u.ENABLED = #{enabled}</if>" +
            "</where>" +
            " ORDER BY u.CREATE_TIME DESC" +
            "</script>")
    Page<SysUser> pageUsers(Page<SysUser> page,
                            @Param("username") String username,
                            @Param("realName") String realName,
                            @Param("enabled") String enabled);
}
