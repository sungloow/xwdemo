package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.entity.District;
import com.citycheckin.service.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "07-区县管理", description = "重庆区县信息维护（超级管理员）")
@RestController
@RequestMapping("/api/district")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @Operation(summary = "区县列表（全量）")
    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(districtService.list());
    }

    @Operation(summary = "区县详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        District d = districtService.getById(id);
        if (d == null) throw new BusinessException(404, "区县不存在");
        return Result.ok(d);
    }

    @Operation(summary = "新增区县（超级管理员）")
    @PostMapping("/add")
    public Result<?> add(@RequestBody District district) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        district.setCreateTime(LocalDateTime.now());
        district.setUpdateTime(LocalDateTime.now());
        districtService.save(district);
        return Result.ok("新增成功");
    }

    @Operation(summary = "编辑区县（超级管理员）")
    @PutMapping("/update")
    public Result<?> update(@RequestBody District district) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        district.setUpdateTime(LocalDateTime.now());
        districtService.updateById(district);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除区县（超级管理员）")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "无权限");
        districtService.removeById(id);
        return Result.ok("删除成功");
    }
}
