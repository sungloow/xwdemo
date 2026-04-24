package com.citycheckin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citycheckin.entity.CheckinComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CheckinCommentMapper extends BaseMapper<CheckinComment> {

    @Select("SELECT cc.*, u.USERNAME as username " +
            "FROM checkin_comment cc " +
            "LEFT JOIN sys_user u ON cc.USER_ID = u.ID " +
            "WHERE cc.CHECKIN_ID = #{checkinId} " +
            "ORDER BY cc.CREATE_TIME ASC")
    List<CheckinComment> selectByCheckinId(@Param("checkinId") Integer checkinId);
}
