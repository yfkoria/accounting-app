package com.accounting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 * 记录所有的收入、支出和转账记录
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    @Column(length = 200)
    private String description;
    
    @Column(length = 500)
    private String remark;
    
    @Column(name = "target_account_id")
    private Long targetAccountId;
    
    @Column(name = "related_transaction_id")
    private Long relatedTransactionId;
    
    @Column(length = 100)
    private String location;
    
    @Column(name = "receipt_image", length = 500)
    private String receiptImage;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 交易类型枚举
     */
    public enum TransactionType {
        INCOME("收入"),
        EXPENSE("支出"),
        TRANSFER_IN("转入"),
        TRANSFER_OUT("转出");
        
        private final String description;
        
        TransactionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
