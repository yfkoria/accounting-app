package com.accounting.dto;

import com.accounting.entity.Transaction;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易记录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    
    @NotNull(message = "账户ID不能为空")
    private Long accountId;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotNull(message = "交易类型不能为空")
    private Transaction.TransactionType type;
    
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须为正数")
    private BigDecimal amount;
    
    @NotNull(message = "交易日期不能为空")
    private LocalDate transactionDate;
    
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;
    
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
    
    private Long targetAccountId;
    private String location;
    private String receiptImage;
}
