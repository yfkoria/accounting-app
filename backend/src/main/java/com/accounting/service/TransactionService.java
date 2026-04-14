package com.accounting.service;

import com.accounting.dto.StatisticsDTO;
import com.accounting.dto.TransactionRequest;
import com.accounting.entity.Account;
import com.accounting.entity.Category;
import com.accounting.entity.Transaction;
import com.accounting.repository.AccountRepository;
import com.accounting.repository.CategoryRepository;
import com.accounting.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易记录服务
 * 处理交易记录相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;
    
    /**
     * 创建交易记录
     */
    @Transactional
    public Transaction createTransaction(Long userId, TransactionRequest request) {
        // 验证账户
        Account account = accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        
        // 验证分类
        Category category = categoryRepository.findByIdAndUserIdOrIsSystemTrue(request.getCategoryId(), userId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .accountId(request.getAccountId())
                .categoryId(request.getCategoryId())
                .type(request.getType())
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .remark(request.getRemark())
                .targetAccountId(request.getTargetAccountId())
                .location(request.getLocation())
                .receiptImage(request.getReceiptImage())
                .build();
        
        // 更新账户余额
        updateAccountBalance(request.getAccountId(), request.getAmount(), request.getType());
        
        // 如果是转账，处理目标账户
        if (request.getTargetAccountId() != null && 
            (request.getType() == Transaction.TransactionType.TRANSFER_IN || 
             request.getType() == Transaction.TransactionType.TRANSFER_OUT)) {
            updateAccountBalance(request.getTargetAccountId(), request.getAmount(), 
                    request.getType() == Transaction.TransactionType.TRANSFER_OUT ? 
                    Transaction.TransactionType.TRANSFER_IN : Transaction.TransactionType.TRANSFER_OUT);
        }
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * 更新账户余额
     */
    private void updateAccountBalance(Long accountId, BigDecimal amount, Transaction.TransactionType type) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        
        BigDecimal newBalance;
        switch (type) {
            case INCOME:
            case TRANSFER_IN:
                newBalance = account.getBalance().add(amount);
                break;
            case EXPENSE:
            case TRANSFER_OUT:
                newBalance = account.getBalance().subtract(amount);
                break;
            default:
                newBalance = account.getBalance();
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    /**
     * 更新交易记录
     */
    @Transactional
    public Transaction updateTransaction(Long userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));
        
        // 先撤销原交易对余额的影响
        reverseAccountBalance(transaction.getAccountId(), transaction.getAmount(), transaction.getType());
        
        // 更新交易信息
        transaction.setAccountId(request.getAccountId());
        transaction.setCategoryId(request.getCategoryId());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setRemark(request.getRemark());
        transaction.setLocation(request.getLocation());
        transaction.setReceiptImage(request.getReceiptImage());
        
        // 应用新交易对余额的影响
        updateAccountBalance(request.getAccountId(), request.getAmount(), request.getType());
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * 撤销账户余额变动
     */
    private void reverseAccountBalance(Long accountId, BigDecimal amount, Transaction.TransactionType type) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        
        BigDecimal newBalance;
        switch (type) {
            case INCOME:
            case TRANSFER_IN:
                newBalance = account.getBalance().subtract(amount);
                break;
            case EXPENSE:
            case TRANSFER_OUT:
                newBalance = account.getBalance().add(amount);
                break;
            default:
                newBalance = account.getBalance();
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    /**
     * 删除交易记录
     */
    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));
        
        // 撤销余额变动
        reverseAccountBalance(transaction.getAccountId(), transaction.getAmount(), transaction.getType());
        
        transactionRepository.delete(transaction);
    }
    
    /**
     * 分页获取交易记录
     */
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByTransactionDateDescCreatedAtDesc(userId, pageable);
    }
    
    /**
     * 按日期范围获取交易记录
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                userId, startDate, endDate);
    }
    
    /**
     * 获取交易详情
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long userId, Long transactionId) {
        return transactionRepository.findById(transactionId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));
    }
    
    /**
     * 获取统计数据
     */
    @Transactional(readOnly = true)
    public StatisticsDTO getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        // 计算总收入和支出
        BigDecimal totalIncome = transactionRepository.sumIncomeByUserIdAndDateRange(userId, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumExpenseByUserIdAndDateRange(userId, startDate, endDate);
        
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        
        // 获取本月数据
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        
        BigDecimal monthlyIncome = transactionRepository.sumIncomeByUserIdAndDateRange(userId, monthStart, monthEnd);
        BigDecimal monthlyExpense = transactionRepository.sumExpenseByUserIdAndDateRange(userId, monthStart, monthEnd);
        
        if (monthlyIncome == null) monthlyIncome = BigDecimal.ZERO;
        if (monthlyExpense == null) monthlyExpense = BigDecimal.ZERO;
        
        // 获取总资产和负债
        BigDecimal totalAssets = accountService.getTotalAssets(userId);
        BigDecimal totalLiabilities = accountService.getTotalLiabilities(userId);
        
        // 获取分类统计
        List<StatisticsDTO.CategoryStatistics> categoryStats = getCategoryStatistics(userId, startDate, endDate);
        
        // 获取趋势数据
        List<StatisticsDTO.TrendData> trendData = getTrendData(userId, startDate, endDate);
        
        // 获取账户分布
        List<StatisticsDTO.AccountDistribution> accountDistribution = getAccountDistribution(userId);
        
        return StatisticsDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netIncome(totalIncome.subtract(totalExpense))
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .monthlyIncome(monthlyIncome)
                .monthlyExpense(monthlyExpense)
                .monthlyBalance(monthlyIncome.subtract(monthlyExpense))
                .categoryStatistics(categoryStats)
                .trendData(trendData)
                .accountDistribution(accountDistribution)
                .build();
    }
    
    /**
     * 获取分类统计
     */
    private List<StatisticsDTO.CategoryStatistics> getCategoryStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> expenseByCategory = transactionRepository.sumExpenseByCategory(userId, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumExpenseByUserIdAndDateRange(userId, startDate, endDate);
        
        if (totalExpense == null || totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }
        
        List<StatisticsDTO.CategoryStatistics> result = new ArrayList<>();
        
        for (Object[] row : expenseByCategory) {
            Long categoryId = (Long) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null) {
                BigDecimal percentage = amount.multiply(BigDecimal.valueOf(100))
                        .divide(totalExpense, 2, RoundingMode.HALF_UP);
                
                result.add(StatisticsDTO.CategoryStatistics.builder()
                        .categoryId(categoryId)
                        .categoryName(category.getName())
                        .categoryIcon(category.getIcon())
                        .categoryColor(category.getIconColor())
                        .amount(amount)
                        .percentage(percentage)
                        .build());
            }
        }
        
        return result;
    }
    
    /**
     * 获取趋势数据
     */
    private List<StatisticsDTO.TrendData> getTrendData(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> dailyData = transactionRepository.sumByDate(userId, startDate, endDate);
        
        List<StatisticsDTO.TrendData> result = new ArrayList<>();
        BigDecimal cumulativeBalance = BigDecimal.ZERO;
        
        for (Object[] row : dailyData) {
            LocalDate date = (LocalDate) row[0];
            BigDecimal income = (BigDecimal) row[1];
            BigDecimal expense = (BigDecimal) row[2];
            
            if (income == null) income = BigDecimal.ZERO;
            if (expense == null) expense = BigDecimal.ZERO;
            
            cumulativeBalance = cumulativeBalance.add(income).subtract(expense);
            
            result.add(StatisticsDTO.TrendData.builder()
                    .date(date.toString())
                    .income(income)
                    .expense(expense)
                    .balance(cumulativeBalance)
                    .build());
        }
        
        return result;
    }
    
    /**
     * 获取账户分布
     */
    private List<StatisticsDTO.AccountDistribution> getAccountDistribution(Long userId) {
        List<Account> accounts = accountRepository.findByUserIdOrderByCreatedAtDesc(userId);
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalBalance.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }
        
        return accounts.stream()
                .map(account -> {
                    BigDecimal percentage = account.getBalance().multiply(BigDecimal.valueOf(100))
                            .divide(totalBalance, 2, RoundingMode.HALF_UP);
                    
                    return StatisticsDTO.AccountDistribution.builder()
                            .accountId(account.getId())
                            .accountName(account.getName())
                            .accountType(account.getType().name())
                            .balance(account.getBalance())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
