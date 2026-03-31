package com.citycheckin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citycheckin.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.ID = rm.M_ID " +
            "INNER JOIN sys_user_role ur ON rm.R_ID = ur.R_ID " +
            "WHERE ur.U_ID = #{userId} " +
            "ORDER BY m.SORT_ORDER ASC")
    List<SysMenu> selectMenusByUserId(@Param("userId") Integer userId);

    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.ID = rm.M_ID " +
            "WHERE rm.R_ID = #{roleId} " +
            "ORDER BY m.SORT_ORDER ASC")
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Integer roleId);
}
