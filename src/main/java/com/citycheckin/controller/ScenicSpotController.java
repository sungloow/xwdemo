package com.citycheckin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.entity.ScenicSpot;
import com.citycheckin.service.ScenicSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "09-风景地管理", description = "景点信息维护（超级管理员维护全部，区县管理员维护本区县）")
@RestController
@RequestMapping("/api/scenicSpot")
@RequiredArgsConstructor
public class ScenicSpotController {

    private final ScenicSpotService scenicSpotService;

    @Operation(summary = "风景地列表", description = "可按区县ID过滤")
    @GetMapping("/list")
    public Result<?> list(@Parameter(description = "区县ID，不传则返回全部") @RequestParam(required = false) Integer districtId) {
        if (districtId != null) {
            return Result.ok(scenicSpotService.listByDistrict(districtId));
        }
        return Result.ok(scenicSpotService.list(
                new LambdaQueryWrapper<ScenicSpot>().orderByAsc(ScenicSpot::getDistrictId)));
    }

    @Operation(summary = "风景地详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        ScenicSpot s = scenicSpotService.getById(id);
        if (s == null) throw new BusinessException(404, "风景地不存在");
        return Result.ok(s);
    }

    @Operation(summary = "新增风景地", description = "超级管理员可操作所有区县；区县管理员只能操作本区县")
    @PostMapping("/add")
    public Result<?> add(@RequestBody ScenicSpot scenicSpot) {
        if (!UserContext.isSuperAdmin()) {
            Integer districtId = UserContext.getDistrictId();
            if (districtId == null || !districtId.equals(scenicSpot.getDistrictId())) {
                throw new BusinessException(403, "只能维护本区县的风景地");
            }
        }
        scenicSpot.setCreator(UserContext.getUsername());
        scenicSpot.setCreateTime(LocalDateTime.now());
        scenicSpot.setUpdateTime(LocalDateTime.now());
        scenicSpotService.save(scenicSpot);
        return Result.ok("新增成功");
    }

    @Operation(summary = "编辑风景地")
    @PutMapping("/update")
    public Result<?> update(@RequestBody ScenicSpot scenicSpot) {
        ScenicSpot existing = scenicSpotService.getById(scenicSpot.getId());
        if (existing == null) throw new BusinessException(404, "风景地不存在");
        if (!UserContext.isSuperAdmin()) {
            Integer districtId = UserContext.getDistrictId();
            if (districtId == null || !districtId.equals(existing.getDistrictId())) {
                throw new BusinessException(403, "只能维护本区县的风景地");
            }
        }
        scenicSpot.setUpdater(UserContext.getUsername());
        scenicSpot.setUpdateTime(LocalDateTime.now());
        scenicSpotService.updateById(scenicSpot);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除风景地")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        ScenicSpot existing = scenicSpotService.getById(id);
        if (existing == null) throw new BusinessException(404, "风景地不存在");
        if (!UserContext.isSuperAdmin()) {
            Integer districtId = UserContext.getDistrictId();
            if (districtId == null || !districtId.equals(existing.getDistrictId())) {
                throw new BusinessException(403, "只能维护本区县的风景地");
            }
        }
        scenicSpotService.removeById(id);
        return Result.ok("删除成功");
    }
}
