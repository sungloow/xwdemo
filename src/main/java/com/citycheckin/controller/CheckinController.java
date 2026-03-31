package com.citycheckin.controller;

import com.citycheckin.common.PageResult;
import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.dto.CheckinAddDTO;
import com.citycheckin.dto.ReviewDTO;
import com.citycheckin.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "06-打卡管理", description = "打卡发布、列表查询、内容审核")
@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @Operation(summary = "发起打卡", description = "普通用户每天最多打卡 10 次，提交后进入待审核状态")
    @PostMapping("/add")
    public Result<?> add(@RequestBody CheckinAddDTO dto) {
        checkinService.addCheckin(dto);
        return Result.ok("打卡成功，等待审核");
    }

    @Operation(summary = "打卡列表（管理端）",
            description = "超级管理员可查全部；区县管理员只能查自己区县；支持类型/状态过滤")
    @GetMapping("/list")
    public Result<?> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "区县ID") @RequestParam(required = false) Integer districtId,
            @Parameter(description = "类型 food/scenic") @RequestParam(required = false) String type,
            @Parameter(description = "状态 0待审核 1已发布 2已拒绝") @RequestParam(required = false) Integer status) {
        if (!UserContext.isSuperAdmin() && UserContext.getDistrictId() != null) {
            districtId = UserContext.getDistrictId();
        }
        return Result.ok(PageResult.of(
                checkinService.pageCheckins(current, size, districtId, type, status, null)));
    }

    @Operation(summary = "我的打卡记录", description = "查询当前登录用户自己的打卡历史")
    @GetMapping("/myList")
    public Result<?> myList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "类型 food/scenic") @RequestParam(required = false) String type,
            @Parameter(description = "状态 0待审核 1已发布 2已拒绝") @RequestParam(required = false) Integer status) {
        return Result.ok(PageResult.of(
                checkinService.pageCheckins(current, size, null, type, status, UserContext.getUserId())));
    }

    @Operation(summary = "待审核列表", description = "区县管理员：只看本区县待审核；超级管理员：看全部待审核")
    @GetMapping("/pending")
    public Result<?> pending(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        Integer districtId = UserContext.isSuperAdmin() ? null : UserContext.getDistrictId();
        return Result.ok(PageResult.of(
                checkinService.pageCheckins(current, size, districtId, null, 0, null)));
    }

    @Operation(summary = "审核打卡内容", description = "status=1 通过，status=2 拒绝（需填写 rejectReason）")
    @PutMapping("/review")
    public Result<?> review(@RequestBody ReviewDTO dto) {
        checkinService.review(dto);
        return Result.ok("审核完成");
    }
}
