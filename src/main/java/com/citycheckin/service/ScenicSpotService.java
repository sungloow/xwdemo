package com.citycheckin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citycheckin.entity.ScenicSpot;

import java.util.List;

public interface ScenicSpotService extends IService<ScenicSpot> {

    List<ScenicSpot> listByDistrict(Integer districtId);
}
