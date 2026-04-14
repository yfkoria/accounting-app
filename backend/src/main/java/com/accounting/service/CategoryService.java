package com.accounting.service;

import com.accounting.dto.CategoryRequest;
import com.accounting.entity.Category;
import com.accounting.repository.CategoryRepository;
import com.accounting.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务
 * 处理分类相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * 初始化系统默认分类
     */
    @PostConstruct
    public void initDefaultCategories() {
        if (categoryRepository.findByIsSystemTrueOrderBySortOrderAsc().isEmpty()) {
            // 支出分类
            createSystemCategory("餐饮", Category.CategoryType.EXPENSE, "🍽️", "#FF6B6B", 1);
            createSystemCategory("交通", Category.CategoryType.EXPENSE, "🚗", "#4ECDC4", 2);
            createSystemCategory("购物", Category.CategoryType.EXPENSE, "🛒", "#45B7D1", 3);
            createSystemCategory("娱乐", Category.CategoryType.EXPENSE, "🎮", "#96CEB4", 4);
            createSystemCategory("居住", Category.CategoryType.EXPENSE, "🏠", "#FFEAA7", 5);
            createSystemCategory("通讯", Category.CategoryType.EXPENSE, "📱", "#DDA0DD", 6);
            createSystemCategory("医疗", Category.CategoryType.EXPENSE, "🏥", "#FFB6C1", 7);
            createSystemCategory("教育", Category.CategoryType.EXPENSE, "📚", "#87CEEB", 8);
            createSystemCategory("服饰", Category.CategoryType.EXPENSE, "👔", "#F0E68C", 9);
            createSystemCategory("其他支出", Category.CategoryType.EXPENSE, "📝", "#D3D3D3", 10);
            
            // 收入分类
            createSystemCategory("工资", Category.CategoryType.INCOME, "💰", "#2ECC71", 1);
            createSystemCategory("奖金", Category.CategoryType.INCOME, "🎁", "#27AE60", 2);
            createSystemCategory("投资收益", Category.CategoryType.INCOME, "📈", "#3498DB", 3);
            createSystemCategory("兼职", Category.CategoryType.INCOME, "💼", "#9B59B6", 4);
            createSystemCategory("红包", Category.CategoryType.INCOME, "🧧", "#E74C3C", 5);
            createSystemCategory("其他收入", Category.CategoryType.INCOME, "💵", "#1ABC9C", 6);
        }
    }
    
    private void createSystemCategory(String name, Category.CategoryType type, 
                                       String icon, String iconColor, int sortOrder) {
        Category category = Category.builder()
                .name(name)
                .type(type)
                .icon(icon)
                .iconColor(iconColor)
                .sortOrder(sortOrder)
                .isSystem(true)
                .build();
        categoryRepository.save(category);
    }
    
    /**
     * 创建自定义分类
     */
    @Transactional
    public Category createCategory(Long userId, CategoryRequest request) {
        // 检查分类名称是否已存在
        if (categoryRepository.existsByUserIdAndNameAndType(userId, request.getName(), request.getType())) {
            throw new RuntimeException("分类名称已存在");
        }
        
        Category category = Category.builder()
                .userId(userId)
                .name(request.getName())
                .type(request.getType())
                .parentId(request.getParentId())
                .icon(request.getIcon())
                .iconColor(request.getIconColor())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isSystem(false)
                .build();
        
        return categoryRepository.save(category);
    }
    
    /**
     * 更新分类
     */
    @Transactional
    public Category updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndUserIdOrIsSystemTrue(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 系统分类不允许修改
        if (Boolean.TRUE.equals(category.getIsSystem())) {
            throw new RuntimeException("系统分类不允许修改");
        }
        
        category.setName(request.getName());
        category.setType(request.getType());
        category.setParentId(request.getParentId());
        category.setIcon(request.getIcon());
        category.setIconColor(request.getIconColor());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        
        return categoryRepository.save(category);
    }
    
    /**
     * 删除分类
     */
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserIdOrIsSystemTrue(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 系统分类不允许删除
        if (Boolean.TRUE.equals(category.getIsSystem())) {
            throw new RuntimeException("系统分类不允许删除");
        }
        
        // 检查是否有交易记录使用该分类
        long count = transactionRepository.countByUserIdAndCategoryId(userId, categoryId);
        if (count > 0) {
            throw new RuntimeException("该分类下存在交易记录，无法删除");
        }
        
        categoryRepository.delete(category);
    }
    
    /**
     * 获取用户所有分类
     */
    @Transactional(readOnly = true)
    public List<Category> getUserCategories(Long userId) {
        return categoryRepository.findByUserIdOrIsSystemTrueOrderBySortOrderAsc(userId);
    }
    
    /**
     * 按类型获取分类
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByType(Long userId, Category.CategoryType type) {
        return categoryRepository.findByUserIdOrIsSystemTrueAndTypeOrderBySortOrderAsc(userId, type);
    }
    
    /**
     * 获取分类详情
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(Long userId, Long categoryId) {
        return categoryRepository.findByIdAndUserIdOrIsSystemTrue(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }
}
