package com.accounting.service;

import com.accounting.dto.AccountRequest;
import com.accounting.entity.Account;
import com.accounting.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务
 * 处理账户相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    /**
     * 创建账户
     */
    @Transactional
    public Account createAccount(Long userId, AccountRequest request) {
        // 检查账户名称是否已存在
        if (accountRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new RuntimeException("账户名称已存在");
        }
        
        Account account = Account.builder()
                .userId(userId)
                .name(request.getName())
                .type(request.getType())
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .description(request.getDescription())
                .icon(request.getIcon())
                .iconColor(request.getIconColor())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();
        
        // 如果设置为默认账户，取消其他默认账户
        if (Boolean.TRUE.equals(account.getIsDefault())) {
            accountRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        accountRepository.save(existingDefault);
                    });
        }
        
        return accountRepository.save(account);
    }
    
    /**
     * 更新账户
     */
    @Transactional
    public Account updateAccount(Long userId, Long accountId, AccountRequest request) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        
        // 检查新名称是否与其他账户重复
        if (!account.getName().equals(request.getName()) && 
            accountRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new RuntimeException("账户名称已存在");
        }
        
        account.setName(request.getName());
        account.setType(request.getType());
        account.setDescription(request.getDescription());
        account.setIcon(request.getIcon());
        account.setIconColor(request.getIconColor());
        
        if (request.getIsDefault() != null && request.getIsDefault()) {
            // 取消其他默认账户
            accountRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existingDefault -> {
                        if (!existingDefault.getId().equals(accountId)) {
                            existingDefault.setIsDefault(false);
                            accountRepository.save(existingDefault);
                        }
                    });
            account.setIsDefault(true);
        }
        
        return accountRepository.save(account);
    }
    
    /**
     * 删除账户
     */
    @Transactional
    public void deleteAccount(Long userId, Long accountId) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        
        accountRepository.delete(account);
    }
    
    /**
     * 获取用户所有账户
     */
    @Transactional(readOnly = true)
    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 获取账户详情
     */
    @Transactional(readOnly = true)
    public Account getAccountById(Long userId, Long accountId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
    }
    
    /**
     * 更新账户余额
     */
    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount, boolean isAdd) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        
        BigDecimal newBalance;
        if (isAdd) {
            newBalance = account.getBalance().add(amount);
        } else {
            newBalance = account.getBalance().subtract(amount);
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    /**
     * 获取用户总资产
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAssets(Long userId) {
        Double total = accountRepository.sumBalanceByUserId(userId);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }
    
    /**
     * 获取用户总负债
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalLiabilities(Long userId) {
        Double total = accountRepository.sumCreditCardBalanceByUserId(userId);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }
}
