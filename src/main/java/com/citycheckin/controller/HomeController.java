package com.citycheckin.controller;

import com.citycheckin.common.TokenInfo;
import com.citycheckin.common.TokenManager;
import com.citycheckin.common.Result;
import com.citycheckin.common.PageResult;
import com.citycheckin.entity.CheckinComment;
import com.citycheckin.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "00-首页统计", description = "区县热度、美食热度、景点热度排名（无需登录）")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final CheckinService checkinService;
    private final TokenManager tokenManager;

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

    @Operation(summary = "前台已发布打卡列表", description = "分页返回所有已审核通过的打卡内容，无需登录")
    @GetMapping("/publishedCheckins")
    public Result<?> publishedCheckins(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "区县ID") @RequestParam(required = false) Integer districtId,
            @Parameter(description = "类型 food/scenic") @RequestParam(required = false) String type) {
        return Result.ok(PageResult.of(
                checkinService.pageCheckins(current, size, districtId, type, 1, null)));
    }

    @Operation(summary = "前台打卡详情", description = "返回已发布打卡详情；若请求头携带有效 token，则额外返回当前用户点赞状态")
    @GetMapping("/checkin/{id}")
    public Result<?> checkinDetail(@PathVariable Integer id, HttpServletRequest request) {
        return Result.ok(checkinService.getPublishedDetail(id, getOptionalUserId(request)));
    }

    @Operation(summary = "前台打卡评论列表", description = "返回已发布打卡的评论列表，无需登录")
    @GetMapping("/checkin/{id}/comments")
    public Result<List<CheckinComment>> checkinComments(@PathVariable Integer id) {
        return Result.ok(checkinService.listPublishedComments(id));
    }

    private Integer getOptionalUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        TokenInfo info = tokenManager.getTokenInfo(token);
        return info == null ? null : info.getUserId();
    }
}
