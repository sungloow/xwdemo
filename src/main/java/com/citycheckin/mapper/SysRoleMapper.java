package com.citycheckin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citycheckin.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.ID = ur.R_ID " +
            "WHERE ur.U_ID = #{userId} AND r.ENABLED = '1'")
    List<SysRole> selectRolesByUserId(@Param("userId") Integer userId);
}
