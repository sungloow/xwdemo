package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.entity.FoodType;
import com.citycheckin.service.FoodTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "08-美食种类", description = "美食分类维护（超级管理员）")
@RestController
@RequestMapping("/api/foodType")
@RequiredArgsConstructor
public class FoodTypeController {

    private final FoodTypeService foodTypeService;

    @Operation(summary = "美食种类列表（全量）")
    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(foodTypeService.list());
    }

    @Operation(summary = "美食种类详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        FoodType ft = foodTypeService.getById(id);
        if (ft == null) throw new BusinessException(404, "美食种类不存在");
        return Result.ok(ft);
    }

    @Operation(summary = "新增美食种类（超级管理员）")
    @PostMapping("/add")
    public Result<?> add(@RequestBody FoodType foodType) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "仅超级管理员可维护美食种类");
        foodType.setCreateTime(LocalDateTime.now());
        foodType.setUpdateTime(LocalDateTime.now());
        foodTypeService.save(foodType);
        return Result.ok("新增成功");
    }

    @Operation(summary = "编辑美食种类（超级管理员）")
    @PutMapping("/update")
    public Result<?> update(@RequestBody FoodType foodType) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "仅超级管理员可维护美食种类");
        foodType.setUpdateTime(LocalDateTime.now());
        foodTypeService.updateById(foodType);
        return Result.ok("更新成功");
    }

    @Operation(summary = "删除美食种类（超级管理员）")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        if (!UserContext.isSuperAdmin()) throw new BusinessException(403, "仅超级管理员可维护美食种类");
        foodTypeService.removeById(id);
        return Result.ok("删除成功");
    }
}
