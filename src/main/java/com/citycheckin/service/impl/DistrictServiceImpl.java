package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.entity.District;
import com.citycheckin.mapper.DistrictMapper;
import com.citycheckin.service.DistrictService;
import org.springframework.stereotype.Service;

@Service
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper, District> implements DistrictService {
}
