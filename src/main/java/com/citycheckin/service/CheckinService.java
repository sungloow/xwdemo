package com.citycheckin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citycheckin.dto.CheckinAddDTO;
import com.citycheckin.dto.RankItemDTO;
import com.citycheckin.dto.ReviewDTO;
import com.citycheckin.entity.Checkin;

import java.util.List;

public interface CheckinService extends IService<Checkin> {

    void addCheckin(CheckinAddDTO dto);

    Page<Checkin> pageCheckins(int current, int size, Integer districtId, String type, Integer status, Integer userId);

    void review(ReviewDTO dto);

    List<RankItemDTO> districtRank(int limit);

    List<RankItemDTO> foodTypeRank(int limit);

    List<RankItemDTO> scenicSpotRank(int limit);
}
