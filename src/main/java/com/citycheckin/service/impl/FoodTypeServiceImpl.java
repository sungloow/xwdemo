package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.entity.FoodType;
import com.citycheckin.mapper.FoodTypeMapper;
import com.citycheckin.service.FoodTypeService;
import org.springframework.stereotype.Service;

@Service
public class FoodTypeServiceImpl extends ServiceImpl<FoodTypeMapper, FoodType> implements FoodTypeService {
}
