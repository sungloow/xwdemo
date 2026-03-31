package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.entity.ScenicSpot;
import com.citycheckin.mapper.ScenicSpotMapper;
import com.citycheckin.service.ScenicSpotService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScenicSpotServiceImpl extends ServiceImpl<ScenicSpotMapper, ScenicSpot> implements ScenicSpotService {

    @Override
    public List<ScenicSpot> listByDistrict(Integer districtId) {
        return baseMapper.selectByDistrictId(districtId);
    }
}
