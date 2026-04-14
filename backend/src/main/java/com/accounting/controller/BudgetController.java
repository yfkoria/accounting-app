package com.accounting.controller;

import com.accounting.dto.ApiResponse;
import com.accounting.dto.BudgetRequest;
import com.accounting.entity.Budget;
import com.accounting.service.AuthService;
import com.accounting.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算控制器
 * 处理预算管理相关请求
 */
@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Tag(name = "预算管理", description = "预算的增删改查和预警接口")
public class BudgetController {
    
    private final BudgetService budgetService;
    private final AuthService authService;
    
    /**
     * 创建预算
     */
    @PostMapping
    @Operation(summary = "创建预算", description = "创建新的预算")
    public ApiResponse<Budget> createBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Budget budget = budgetService.createBudget(userId, request);
        return ApiResponse.success("预算创建成功", budget);
    }
    
    /**
     * 更新预算
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新预算", description = "更新预算信息")
    public ApiResponse<Budget> updateBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Budget budget = budgetService.updateBudget(userId, id, request);
        return ApiResponse.success("预算更新成功", budget);
    }
    
    /**
     * 删除预算
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除预算", description = "删除指定预算")
    public ApiResponse<Void> deleteBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        budgetService.deleteBudget(userId, id);
        return ApiResponse.success("预算删除成功", null);
    }
    
    /**
     * 获取所有预算
     */
    @GetMapping
    @Operation(summary = "获取所有预算", description = "获取当前用户的所有预算")
    public ApiResponse<List<Budget>> getAllBudgets(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        List<Budget> budgets = budgetService.getUserBudgets(userId);
        return ApiResponse.success(budgets);
    }
    
    /**
     * 获取预算详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取预算详情", description = "获取指定预算的详细信息")
    public ApiResponse<Budget> getBudgetById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Budget budget = budgetService.getBudgetById(userId, id);
        return ApiResponse.success(budget);
    }
    
    /**
     * 检查预算预警
     */
    @GetMapping("/alerts")
    @Operation(summary = "检查预算预警", description = "检查是否有预算超支预警")
    public ApiResponse<List<Budget>> checkBudgetAlerts(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        List<Budget> alerts = budgetService.checkBudgetAlerts(userId);
        return ApiResponse.success(alerts);
    }
}
