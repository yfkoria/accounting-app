package com.accounting.controller;

import com.accounting.dto.ApiResponse;
import com.accounting.dto.StatisticsDTO;
import com.accounting.dto.TransactionRequest;
import com.accounting.entity.Transaction;
import com.accounting.service.AuthService;
import com.accounting.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 交易记录控制器
 * 处理交易记录相关请求
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "交易记录管理", description = "交易记录的增删改查和统计接口")
public class TransactionController {
    
    private final TransactionService transactionService;
    private final AuthService authService;
    
    /**
     * 创建交易记录
     */
    @PostMapping
    @Operation(summary = "创建交易记录", description = "记录新的收入或支出")
    public ApiResponse<Transaction> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Transaction transaction = transactionService.createTransaction(userId, request);
        return ApiResponse.success("交易记录创建成功", transaction);
    }
    
    /**
     * 更新交易记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新交易记录", description = "更新交易记录信息")
    public ApiResponse<Transaction> updateTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Transaction transaction = transactionService.updateTransaction(userId, id, request);
        return ApiResponse.success("交易记录更新成功", transaction);
    }
    
    /**
     * 删除交易记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易记录", description = "删除指定交易记录")
    public ApiResponse<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        transactionService.deleteTransaction(userId, id);
        return ApiResponse.success("交易记录删除成功", null);
    }
    
    /**
     * 分页获取交易记录
     */
    @GetMapping
    @Operation(summary = "分页获取交易记录", description = "分页获取当前用户的交易记录")
    public ApiResponse<Page<Transaction>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getTransactions(userId, pageable);
        return ApiResponse.success(transactions);
    }
    
    /**
     * 按日期范围获取交易记录
     */
    @GetMapping("/range")
    @Operation(summary = "按日期范围获取交易记录", description = "获取指定日期范围内的交易记录")
    public ApiResponse<List<Transaction>> getTransactionsByDateRange(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(userId, startDate, endDate);
        return ApiResponse.success(transactions);
    }
    
    /**
     * 获取交易详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取交易详情", description = "获取指定交易记录的详细信息")
    public ApiResponse<Transaction> getTransactionById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Transaction transaction = transactionService.getTransactionById(userId, id);
        return ApiResponse.success(transaction);
    }
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取统计数据", description = "获取指定日期范围内的收支统计数据")
    public ApiResponse<StatisticsDTO> getStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        StatisticsDTO statistics = transactionService.getStatistics(userId, startDate, endDate);
        return ApiResponse.success(statistics);
    }
}
