package com.accounting.service;

import com.accounting.dto.BudgetRequest;
import com.accounting.entity.Budget;
import com.accounting.repository.BudgetRepository;
import com.accounting.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Budget createBudget(Long userId, BudgetRequest request) {
        if (request.getCategoryId() != null) {
            budgetRepository.findByUserIdAndCategoryId(userId, request.getCategoryId())
                    .ifPresent(b -> { throw new RuntimeException("该分类已存在预算"); });
        }

        Budget budget = Budget.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .period(request.getPeriod())
                .amount(request.getAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .alertThreshold(request.getAlertThreshold() != null ? request.getAlertThreshold() : 80)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("预算不存在"));

        budget.setCategoryId(request.getCategoryId());
        budget.setName(request.getName());
        budget.setPeriod(request.getPeriod());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setAlertThreshold(request.getAlertThreshold());
        budget.setIsActive(request.getIsActive());

        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("预算不存在"));
        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public List<Budget> getUserBudgets(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Budget budget : budgets) {
            budget.setSpent(calculateSpent(userId, budget));
        }
        return budgets;
    }

    @Transactional(readOnly = true)
    public Budget getBudgetById(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("预算不存在"));
        budget.setSpent(calculateSpent(userId, budget));
        return budget;
    }

    private BigDecimal calculateSpent(Long userId, Budget budget) {
        // 修复核心：变量只赋值1次，变成有效final，解决Lambda报错
        LocalDate startDate;
        LocalDate endDate;

        // 优先使用自定义日期
        if (budget.getStartDate() != null && budget.getEndDate() != null) {
            startDate = budget.getStartDate().toLocalDate();
            endDate = budget.getEndDate().toLocalDate();
        } else {
            // 没有自定义日期，按周期计算
            switch (budget.getPeriod()) {
                case DAILY:
                    startDate = LocalDate.now();
                    endDate = LocalDate.now();
                    break;
                case WEEKLY:
                    LocalDate today = LocalDate.now();
                    startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                    endDate = startDate.plusDays(6);
                    break;
                case MONTHLY:
                    YearMonth currentMonth = YearMonth.now();
                    startDate = currentMonth.atDay(1);
                    endDate = currentMonth.atEndOfMonth();
                    break;
                case YEARLY:
                    int year = LocalDate.now().getYear();
                    startDate = LocalDate.of(year, 1, 1);
                    endDate = LocalDate.of(year, 12, 31);
                    break;
                default:
                    startDate = LocalDate.now();
                    endDate = LocalDate.now();
            }
        }

        BigDecimal spent;
        if (budget.getCategoryId() != null) {
            spent = transactionRepository.findByUserIdAndCategoryIdOrderByTransactionDateDesc(
                            userId, budget.getCategoryId()).stream()
                    .filter(t -> t.getType().name().equals("EXPENSE"))
                    .filter(t -> !t.getTransactionDate().isBefore(startDate) &&
                                 !t.getTransactionDate().isAfter(endDate))
                    .map(t -> t.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            spent = transactionRepository.sumExpenseByUserIdAndDateRange(userId, startDate, endDate);
        }

        return spent != null ? spent : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<Budget> checkBudgetAlerts(Long userId) {
        List<Budget> activeBudgets = budgetRepository.findByUserIdAndIsActiveTrue(userId);

        return activeBudgets.stream()
                .filter(budget -> {
                    BigDecimal spent = calculateSpent(userId, budget);
                    int percentage = spent.multiply(BigDecimal.valueOf(100))
                            .divide(budget.getAmount(), 0, BigDecimal.ROUND_HALF_UP)
                            .intValue();
                    return percentage >= budget.getAlertThreshold();
                })
                .peek(budget -> budget.setSpent(calculateSpent(userId, budget)))
                .toList();
    }
}