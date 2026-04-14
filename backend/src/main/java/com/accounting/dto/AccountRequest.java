package com.accounting.dto;

import com.accounting.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

/**
 * 账户请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
    
    @NotBlank(message = "账户名称不能为空")
    private String name;
    
    @NotNull(message = "账户类型不能为空")
    private Account.AccountType type;
    
    @PositiveOrZero(message = "余额不能为负数")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
    
    private String description;
    private String icon;
    private String iconColor;
    private Boolean isDefault;
}
