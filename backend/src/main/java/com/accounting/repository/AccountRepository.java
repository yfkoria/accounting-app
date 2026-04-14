package com.accounting.repository;

import com.accounting.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 账户数据访问层
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * 根据用户ID查找所有账户
     */
    List<Account> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID和账户ID查找账户
     */
    Optional<Account> findByIdAndUserId(Long id, Long userId);
    
    /**
     * 查找用户的默认账户
     */
    Optional<Account> findByUserIdAndIsDefaultTrue(Long userId);
    
    /**
     * 根据用户ID和类型查找账户
     */
    List<Account> findByUserIdAndType(Long userId, Account.AccountType type);
    
    /**
     * 计算用户总资产
     */
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.userId = :userId AND a.type NOT IN ('CREDIT_CARD')")
    Double sumBalanceByUserId(@Param("userId") Long userId);
    
    /**
     * 计算用户总负债（信用卡）
     */
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.userId = :userId AND a.type = 'CREDIT_CARD'")
    Double sumCreditCardBalanceByUserId(@Param("userId") Long userId);
    
    /**
     * 检查账户名称是否存在
     */
    boolean existsByUserIdAndName(Long userId, String name);
}
