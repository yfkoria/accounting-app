package com.accounting.dto;

import com.accounting.entity.Budget;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequest {
    
    private Long categoryId;
    
    @NotBlank(message = "预算名称不能为空")
    private String name;
    
    @NotNull(message = "预算周期不能为空")
    private Budget.BudgetPeriod period;
    
    @NotNull(message = "预算金额不能为空")
    @Positive(message = "预算金额必须为正数")
    private BigDecimal amount;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @Min(value = 1, message = "预警阈值必须在1-100之间")
    @Max(value = 100, message = "预警阈值必须在1-100之间")
    private Integer alertThreshold;
    
    private Boolean isActive;
}
