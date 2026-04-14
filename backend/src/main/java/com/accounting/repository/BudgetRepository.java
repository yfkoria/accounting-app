package com.accounting.repository;

import com.accounting.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 预算数据访问层
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * 根据用户ID查找所有预算
     */
    List<Budget> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID查找活跃预算
     */
    List<Budget> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * 根据用户ID和预算ID查找预算
     */
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    
    /**
     * 根据用户ID和分类ID查找预算
     */
    Optional<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
    
    /**
     * 根据用户ID和周期查找预算
     */
    List<Budget> findByUserIdAndPeriod(Long userId, Budget.BudgetPeriod period);
}
