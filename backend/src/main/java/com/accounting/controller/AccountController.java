package com.accounting.controller;

import com.accounting.dto.AccountRequest;
import com.accounting.dto.ApiResponse;
import com.accounting.entity.Account;
import com.accounting.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户控制器
 * 处理账户管理相关请求
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "账户管理", description = "账户的增删改查接口")
public class AccountController {
    
    private final AccountService accountService;
    private final com.accounting.service.AuthService authService;
    
    /**
     * 创建账户
     */
    @PostMapping
    @Operation(summary = "创建账户", description = "创建新的账户")
    public ApiResponse<Account> createAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AccountRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Account account = accountService.createAccount(userId, request);
        return ApiResponse.success("账户创建成功", account);
    }
    
    /**
     * 更新账户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新账户", description = "更新账户信息")
    public ApiResponse<Account> updateAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Account account = accountService.updateAccount(userId, id, request);
        return ApiResponse.success("账户更新成功", account);
    }
    
    /**
     * 删除账户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户", description = "删除指定账户")
    public ApiResponse<Void> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        accountService.deleteAccount(userId, id);
        return ApiResponse.success("账户删除成功", null);
    }
    
    /**
     * 获取所有账户
     */
    @GetMapping
    @Operation(summary = "获取所有账户", description = "获取当前用户的所有账户")
    public ApiResponse<List<Account>> getAllAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        List<Account> accounts = accountService.getUserAccounts(userId);
        return ApiResponse.success(accounts);
    }
    
    /**
     * 获取账户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取账户详情", description = "获取指定账户的详细信息")
    public ApiResponse<Account> getAccountById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Account account = accountService.getAccountById(userId, id);
        return ApiResponse.success(account);
    }
    
    /**
     * 获取总资产
     */
    @GetMapping("/total-assets")
    @Operation(summary = "获取总资产", description = "获取用户所有账户的总资产")
    public ApiResponse<java.math.BigDecimal> getTotalAssets(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        java.math.BigDecimal totalAssets = accountService.getTotalAssets(userId);
        return ApiResponse.success(totalAssets);
    }
}
