package com.accounting.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计数据DTO
 * 用于返回各类统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    
    /** 总收入 */
    private BigDecimal totalIncome;
    
    /** 总支出 */
    private BigDecimal totalExpense;
    
    /** 净收入 */
    private BigDecimal netIncome;
    
    /** 总资产 */
    private BigDecimal totalAssets;
    
    /** 总负债 */
    private BigDecimal totalLiabilities;
    
    /** 本月收入 */
    private BigDecimal monthlyIncome;
    
    /** 本月支出 */
    private BigDecimal monthlyExpense;
    
    /** 本月结余 */
    private BigDecimal monthlyBalance;
    
    /** 分类统计 */
    private List<CategoryStatistics> categoryStatistics;
    
    /** 趋势数据 */
    private List<TrendData> trendData;
    
    /** 账户分布 */
    private List<AccountDistribution> accountDistribution;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryStatistics {
        private Long categoryId;
        private String categoryName;
        private String categoryIcon;
        private String categoryColor;
        private BigDecimal amount;
        private BigDecimal percentage;
        private Integer transactionCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendData {
        private String date;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountDistribution {
        private Long accountId;
        private String accountName;
        private String accountType;
        private BigDecimal balance;
        private BigDecimal percentage;
    }
}
