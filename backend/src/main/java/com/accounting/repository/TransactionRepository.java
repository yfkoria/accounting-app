package com.accounting.repository;

import com.accounting.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易记录数据访问层
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * 根据用户ID分页查询交易记录
     */
    Page<Transaction> findByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和日期范围查询
     */
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据用户ID和账户ID查询
     */
    List<Transaction> findByUserIdAndAccountIdOrderByTransactionDateDesc(Long userId, Long accountId);
    
    /**
     * 根据用户ID和分类ID查询
     */
    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(Long userId, Long categoryId);
    
    /**
     * 计算某时间段内的收入总额
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId " +
           "AND t.type = 'INCOME' AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumIncomeByUserIdAndDateRange(@Param("userId") Long userId, 
                                              @Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * 计算某时间段内的支出总额
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId " +
           "AND t.type = 'EXPENSE' AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseByUserIdAndDateRange(@Param("userId") Long userId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * 按分类统计支出
     */
    @Query("SELECT t.categoryId, SUM(t.amount) FROM Transaction t " +
           "WHERE t.userId = :userId AND t.type = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.categoryId ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumExpenseByCategory(@Param("userId") Long userId, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * 按分类统计收入
     */
    @Query("SELECT t.categoryId, SUM(t.amount) FROM Transaction t " +
           "WHERE t.userId = :userId AND t.type = 'INCOME' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.categoryId ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumIncomeByCategory(@Param("userId") Long userId, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * 按日期统计收支
     */
    @Query("SELECT t.transactionDate, " +
           "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), " +
           "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) " +
           "FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.transactionDate ORDER BY t.transactionDate")
    List<Object[]> sumByDate(@Param("userId") Long userId, 
                              @Param("startDate") LocalDate startDate, 
                              @Param("endDate") LocalDate endDate);
    
    /**
     * 统计某分类的交易数量
     */
    long countByUserIdAndCategoryId(Long userId, Long categoryId);
}
