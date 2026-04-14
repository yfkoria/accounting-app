package com.accounting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类实体类
 * 用于管理收入和支出的分类
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(length = 50)
    private String icon;
    
    @Column(name = "icon_color", length = 20)
    private String iconColor;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @Column(name = "is_system")
    @Builder.Default
    private Boolean isSystem = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 分类类型枚举
     */
    public enum CategoryType {
        INCOME("收入"),
        EXPENSE("支出"),
        TRANSFER("转账");
        
        private final String description;
        
        CategoryType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
