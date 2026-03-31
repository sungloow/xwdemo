package com.citycheckin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citycheckin.entity.Checkin;
import com.citycheckin.dto.RankItemDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CheckinMapper extends BaseMapper<Checkin> {

    @Select("<script>" +
            "SELECT c.*, d.NAME as district_name, f.NAME as food_type_name, s.NAME as scenic_spot_name, u.USERNAME as username " +
            "FROM checkin c " +
            "LEFT JOIN district d ON c.DISTRICT_ID = d.ID " +
            "LEFT JOIN food_type f ON c.FOOD_TYPE_ID = f.ID " +
            "LEFT JOIN scenic_spot s ON c.SCENIC_SPOT_ID = s.ID " +
            "LEFT JOIN sys_user u ON c.USER_ID = u.ID " +
            "<where>" +
            "<if test='districtId != null'> AND c.DISTRICT_ID = #{districtId}</if>" +
            "<if test='type != null and type != \"\"'> AND c.TYPE = #{type}</if>" +
            "<if test='status != null'> AND c.STATUS = #{status}</if>" +
            "<if test='userId != null'> AND c.USER_ID = #{userId}</if>" +
            "</where>" +
            " ORDER BY c.CREATE_TIME DESC" +
            "</script>")
    Page<Checkin> pageCheckins(Page<Checkin> page,
                               @Param("districtId") Integer districtId,
                               @Param("type") String type,
                               @Param("status") Integer status,
                               @Param("userId") Integer userId);

    /** 区县打卡热度排名（已发布） */
    @Select("SELECT d.ID as id, d.NAME as name, COUNT(c.ID) as count " +
            "FROM district d LEFT JOIN checkin c ON d.ID = c.DISTRICT_ID AND c.STATUS = 1 " +
            "GROUP BY d.ID, d.NAME ORDER BY count DESC LIMIT #{limit}")
    List<RankItemDTO> districtRank(@Param("limit") int limit);

    /** 美食种类热度排名（已发布） */
    @Select("SELECT f.ID as id, f.NAME as name, COUNT(c.ID) as count " +
            "FROM food_type f LEFT JOIN checkin c ON f.ID = c.FOOD_TYPE_ID AND c.STATUS = 1 AND c.TYPE = 'food' " +
            "GROUP BY f.ID, f.NAME ORDER BY count DESC LIMIT #{limit}")
    List<RankItemDTO> foodTypeRank(@Param("limit") int limit);

    /** 风景地热度排名（已发布） */
    @Select("SELECT s.ID as id, s.NAME as name, COUNT(c.ID) as count " +
            "FROM scenic_spot s LEFT JOIN checkin c ON s.ID = c.SCENIC_SPOT_ID AND c.STATUS = 1 AND c.TYPE = 'scenic' " +
            "GROUP BY s.ID, s.NAME ORDER BY count DESC LIMIT #{limit}")
    List<RankItemDTO> scenicSpotRank(@Param("limit") int limit);

    /** 统计某用户今天打卡次数 */
    @Select("SELECT COUNT(*) FROM checkin WHERE USER_ID = #{userId} AND CAST(CREATE_TIME AS DATE) = CAST(NOW() AS DATE)")
    int countTodayCheckins(@Param("userId") Integer userId);
}
