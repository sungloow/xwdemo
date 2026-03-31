package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "00-首页统计", description = "区县热度、美食热度、景点热度排名（无需登录）")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final CheckinService checkinService;

    @Operation(summary = "首页总览", description = "一次性返回区县排名、美食排名、景点排名，无需登录")
    @GetMapping("/overview")
    public Result<?> overview(@Parameter(description = "排名条数，默认10") @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> data = new HashMap<>();
        data.put("districtRank",   checkinService.districtRank(limit));
        data.put("foodTypeRank",   checkinService.foodTypeRank(limit));
        data.put("scenicSpotRank", checkinService.scenicSpotRank(limit));
        return Result.ok(data);
    }

    @Operation(summary = "区县打卡热度排名", description = "按已发布打卡数量从高到低排序，无需登录")
    @GetMapping("/districtRank")
    public Result<?> districtRank(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(checkinService.districtRank(limit));
    }

    @Operation(summary = "美食种类热度排名", description = "按该种类已发布打卡数量排序，无需登录")
    @GetMapping("/foodRank")
    public Result<?> foodRank(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(checkinService.foodTypeRank(limit));
    }

    @Operation(summary = "风景地热度排名", description = "按该景点已发布打卡数量排序，无需登录")
    @GetMapping("/scenicRank")
    public Result<?> scenicRank(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(checkinService.scenicSpotRank(limit));
    }
}
