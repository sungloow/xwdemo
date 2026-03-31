package com.citycheckin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citycheckin.entity.ScenicSpot;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ScenicSpotMapper extends BaseMapper<ScenicSpot> {

    @Select("SELECT s.* FROM scenic_spot s WHERE s.DISTRICT_ID = #{districtId} ORDER BY s.NAME ASC")
    List<ScenicSpot> selectByDistrictId(@Param("districtId") Integer districtId);
}
