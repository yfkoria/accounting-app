/**
 * 智能记账 - 前端应用
 * 个人财务管理助手
 */

// API 基础路径
const API_BASE_URL = 'http://localhost:8080/api';

// 全局状态
let currentUser = null;
let authToken = localStorage.getItem('token');
let accounts = [];
let categories = [];

// ==================== 工具函数 ====================

/**
 * 显示 Toast 提示
 */
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `custom-toast ${type}`;
    toast.innerHTML = `
        <i class="bi ${type === 'success' ? 'bi-check-circle text-success' : 'bi-exclamation-circle text-danger'}"></i>
        <span>${message}</span>
    `;
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

/**
 * 格式化金额
 */
function formatMoney(amount) {
    return '¥' + (amount || 0).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

/**
 * 格式化日期
 */
function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('zh-CN');
}

/**
 * 发送 API 请求
 */
async function apiRequest(endpoint, options = {}) {
    const url = API_BASE_URL + endpoint;
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }
    
    try {
        const response = await fetch(url, {
            ...options,
            headers
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || '请求失败');
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// ==================== 认证相关 ====================

/**
 * 显示登录表单
 */
function showLoginForm() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
}

/**
 * 显示注册表单
 */
function showRegisterForm() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
}

/**
 * 处理登录
 */
async function handleLogin() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    if (!username || !password) {
        showToast('请输入用户名和密码', 'error');
        return;
    }
    
    try {
        const response = await apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        
        if (response.success) {
            authToken = response.data.token;
            currentUser = response.data.user;
            localStorage.setItem('token', authToken);
            localStorage.setItem('user', JSON.stringify(currentUser));
            
            showToast('登录成功');
            showApp();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 处理注册
 */
async function handleRegister() {
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;
    
    if (!username || !password) {
        showToast('请填写必填信息', 'error');
        return;
    }
    
    if (password !== confirmPassword) {
        showToast('两次密码输入不一致', 'error');
        return;
    }
    
    try {
        const response = await apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password, nickname: username })
        });
        
        if (response.success) {
            authToken = response.data.token;
            currentUser = response.data.user;
            localStorage.setItem('token', authToken);
            localStorage.setItem('user', JSON.stringify(currentUser));
            
            showToast('注册成功');
            showApp();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 处理登出
 */
function handleLogout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    document.getElementById('loginPage').style.display = 'flex';
    document.getElementById('appContainer').style.display = 'none';
    
    showToast('已退出登录');
}

/**
 * 显示主应用
 */
function showApp() {
    document.getElementById('loginPage').style.display = 'none';
    document.getElementById('appContainer').style.display = 'block';
    
    // 更新用户信息显示
    if (currentUser) {
        document.getElementById('welcomeUser').textContent = `欢迎回来，${currentUser.nickname || currentUser.username}`;
        document.getElementById('userAvatar').textContent = (currentUser.nickname || currentUser.username).charAt(0).toUpperCase();
    }
    
    // 加载数据
    loadDashboard();
    loadAccounts();
    loadCategories();
}

// ==================== 页面导航 ====================

/**
 * 导航到指定页面
 */
function navigateTo(page) {
    // 更新菜单状态
    document.querySelectorAll('.sidebar-menu a').forEach(a => {
        a.classList.remove('active');
        if (a.dataset.page === page) {
            a.classList.add('active');
        }
    });
    
    // 切换页面
    document.querySelectorAll('.page-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(page + 'Page').classList.add('active');
    
    // 加载页面数据
    switch (page) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'transactions':
            loadTransactions();
            break;
        case 'accounts':
            loadAccounts();
            break;
        case 'categories':
            loadCategories();
            break;
        case 'budgets':
            loadBudgets();
            break;
        case 'statistics':
            initStatistics();
            break;
    }
}

// 绑定导航事件
document.querySelectorAll('.sidebar-menu a').forEach(a => {
    a.addEventListener('click', (e) => {
        e.preventDefault();
        navigateTo(a.dataset.page);
    });
});

// ==================== 仪表盘 ====================

/**
 * 加载仪表盘数据
 */
async function loadDashboard() {
    const today = new Date();
    const startDate = new Date(today.getFullYear(), today.getMonth(), 1);
    const endDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    
    try {
        // 加载统计数据
        const statsResponse = await apiRequest(`/transactions/statistics?startDate=${startDate.toISOString().split('T')[0]}&endDate=${endDate.toISOString().split('T')[0]}`);
        
        if (statsResponse.success) {
            const stats = statsResponse.data;
            document.getElementById('monthlyIncome').textContent = formatMoney(stats.monthlyIncome);
            document.getElementById('monthlyExpense').textContent = formatMoney(stats.monthlyExpense);
            document.getElementById('monthlyBalance').textContent = formatMoney(stats.monthlyBalance);
            document.getElementById('totalAssets').textContent = formatMoney(stats.totalAssets);
        }
        
        // 加载最近交易
        loadRecentTransactions();
        
        // 加载预算概览
        loadBudgetOverview();
        
    } catch (error) {
        console.error('加载仪表盘失败:', error);
    }
}

/**
 * 加载最近交易
 */
async function loadRecentTransactions() {
    try {
        const response = await apiRequest('/transactions?page=0&size=10');
        
        if (response.success) {
            const transactions = response.data.content || [];
            renderTransactions(transactions, 'recentTransactions', true);
        }
    } catch (error) {
        document.getElementById('recentTransactions').innerHTML = `
            <div class="empty-state">
                <i class="bi bi-receipt"></i>
                <h5>暂无交易记录</h5>
                <p>点击上方按钮开始记账</p>
            </div>
        `;
    }
}

/**
 * 加载预算概览
 */
async function loadBudgetOverview() {
    try {
        const response = await apiRequest('/budgets');
        
        if (response.success && response.data.length > 0) {
            const budgets = response.data;
            let html = '';
            
            budgets.slice(0, 3).forEach(budget => {
                const percentage = (budget.spent / budget.amount * 100).toFixed(0);
                const progressClass = percentage >= 80 ? 'bg-danger' : percentage >= 50 ? 'bg-warning' : 'bg-success';
                
                html += `
                    <div class="budget-item">
                        <div class="budget-header">
                            <span class="budget-name">${budget.name}</span>
                            <span class="budget-amount">${formatMoney(budget.spent)} / ${formatMoney(budget.amount)}</span>
                        </div>
                        <div class="progress">
                            <div class="progress-bar ${progressClass}" style="width: ${Math.min(percentage, 100)}%"></div>
                        </div>
                    </div>
                `;
            });
            
            document.getElementById('budgetOverview').innerHTML = html;
        } else {
            document.getElementById('budgetOverview').innerHTML = `
                <div class="empty-state">
                    <i class="bi bi-piggy-bank"></i>
                    <h5>暂无预算</h5>
                    <p>创建预算来控制支出</p>
                </div>
            `;
        }
    } catch (error) {
        console.error('加载预算失败:', error);
    }
}

// ==================== 交易管理 ====================

/**
 * 加载交易列表
 */
async function loadTransactions() {
    try {
        const response = await apiRequest('/transactions?page=0&size=50');
        
        if (response.success) {
            const transactions = response.data.content || [];
            renderTransactions(transactions, 'transactionsList');
        }
    } catch (error) {
        document.getElementById('transactionsList').innerHTML = `
            <div class="empty-state">
                <i class="bi bi-receipt"></i>
                <h5>暂无交易记录</h5>
                <p>点击新增按钮开始记账</p>
            </div>
        `;
    }
}

/**
 * 渲染交易列表
 */
function renderTransactions(transactions, containerId, isRecent = false) {
    if (!transactions || transactions.length === 0) {
        document.getElementById(containerId).innerHTML = `
            <div class="empty-state">
                <i class="bi bi-receipt"></i>
                <h5>暂无交易记录</h5>
            </div>
        `;
        return;
    }
    
    let html = '';
    
    transactions.forEach(t => {
        const typeClass = t.type === 'INCOME' ? 'income' : 'expense';
        const amountPrefix = t.type === 'INCOME' ? '+' : '-';
        const icon = getCategoryIcon(t.categoryId);
        
        html += `
            <div class="transaction-item">
                <div class="transaction-icon" style="background: ${icon.color}20; color: ${icon.color}">
                    ${icon.icon}
                </div>
                <div class="transaction-info">
                    <h5>${t.description || getCategoryName(t.categoryId)}</h5>
                    <p>${formatDate(t.transactionDate)} · ${getAccountName(t.accountId)}</p>
                </div>
                <div class="transaction-amount ${typeClass}">
                    ${amountPrefix}${formatMoney(t.amount)}
                </div>
            </div>
        `;
    });
    
    document.getElementById(containerId).innerHTML = html;
}

/**
 * 筛选交易
 */
async function filterTransactions() {
    const startDate = document.getElementById('filterStartDate').value;
    const endDate = document.getElementById('filterEndDate').value;
    
    if (!startDate || !endDate) {
        showToast('请选择日期范围', 'error');
        return;
    }
    
    try {
        const response = await apiRequest(`/transactions/range?startDate=${startDate}&endDate=${endDate}`);
        
        if (response.success) {
            renderTransactions(response.data, 'transactionsList');
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 打开交易模态框
 */
function openTransactionModal(type = null) {
    document.getElementById('transactionForm').reset();
    document.getElementById('transactionId').value = '';
    document.getElementById('transactionDate').value = new Date().toISOString().split('T')[0];
    
    if (type) {
        document.getElementById('transactionType').value = type;
    }
    
    // 填充账户选项
    populateAccountSelect();
    
    // 填充分类选项
    populateCategorySelect();
    
    const modal = new bootstrap.Modal(document.getElementById('transactionModal'));
    modal.show();
}

/**
 * 保存交易
 */
async function saveTransaction() {
    const id = document.getElementById('transactionId').value;
    const data = {
        type: document.getElementById('transactionType').value,
        amount: parseFloat(document.getElementById('transactionAmount').value),
        accountId: parseInt(document.getElementById('transactionAccount').value),
        categoryId: parseInt(document.getElementById('transactionCategory').value),
        transactionDate: document.getElementById('transactionDate').value,
        remark: document.getElementById('transactionRemark').value
    };
    
    try {
        let response;
        if (id) {
            response = await apiRequest(`/transactions/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        } else {
            response = await apiRequest('/transactions', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }
        
        if (response.success) {
            bootstrap.Modal.getInstance(document.getElementById('transactionModal')).hide();
            showToast('保存成功');
            loadDashboard();
            loadTransactions();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// ==================== 账户管理 ====================

/**
 * 加载账户列表
 */
async function loadAccounts() {
    try {
        const response = await apiRequest('/accounts');
        
        if (response.success) {
            accounts = response.data;
            renderAccounts();
        }
    } catch (error) {
        console.error('加载账户失败:', error);
    }
}

/**
 * 渲染账户列表
 */
function renderAccounts() {
    if (!accounts || accounts.length === 0) {
        document.getElementById('accountsList').innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="bi bi-credit-card"></i>
                    <h5>暂无账户</h5>
                    <p>点击新增按钮创建账户</p>
                </div>
            </div>
        `;
        return;
    }
    
    const colors = [
        'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
        'linear-gradient(135deg, #eb3349 0%, #f45c43 100%)',
        'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        'linear-gradient(135deg, #F2994A 0%, #F2C94C 100%)'
    ];
    
    let html = '';
    
    accounts.forEach((account, index) => {
        const color = colors[index % colors.length];
        const typeNames = {
            'CASH': '现金',
            'BANK_CARD': '银行卡',
            'CREDIT_CARD': '信用卡',
            'ALIPAY': '支付宝',
            'WECHAT': '微信',
            'INVESTMENT': '投资账户',
            'OTHER': '其他'
        };
        
        html += `
            <div class="col-md-6 col-lg-4">
                <div class="account-card" style="background: ${color}">
                    <span class="account-type">${typeNames[account.type] || account.type}</span>
                    <div class="bank-name">${account.description || '个人账户'}</div>
                    <div class="account-name">${account.name}</div>
                    <div class="balance">${formatMoney(account.balance)}</div>
                    <div class="mt-3">
                        <button class="btn btn-sm btn-light me-2" onclick="editAccount(${account.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-light" onclick="deleteAccount(${account.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    });
    
    document.getElementById('accountsList').innerHTML = html;
}

/**
 * 打开账户模态框
 */
function openAccountModal() {
    document.getElementById('accountForm').reset();
    document.getElementById('accountId').value = '';
    document.getElementById('accountModalTitle').textContent = '新增账户';
    
    const modal = new bootstrap.Modal(document.getElementById('accountModal'));
    modal.show();
}

/**
 * 编辑账户
 */
async function editAccount(id) {
    const account = accounts.find(a => a.id === id);
    if (!account) return;
    
    document.getElementById('accountId').value = account.id;
    document.getElementById('accountName').value = account.name;
    document.getElementById('accountType').value = account.type;
    document.getElementById('accountBalance').value = account.balance;
    document.getElementById('accountDescription').value = account.description || '';
    document.getElementById('accountModalTitle').textContent = '编辑账户';
    
    const modal = new bootstrap.Modal(document.getElementById('accountModal'));
    modal.show();
}

/**
 * 保存账户
 */
async function saveAccount() {
    const id = document.getElementById('accountId').value;
    const data = {
        name: document.getElementById('accountName').value,
        type: document.getElementById('accountType').value,
        balance: parseFloat(document.getElementById('accountBalance').value) || 0,
        description: document.getElementById('accountDescription').value
    };
    
    try {
        let response;
        if (id) {
            response = await apiRequest(`/accounts/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        } else {
            response = await apiRequest('/accounts', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }
        
        if (response.success) {
            bootstrap.Modal.getInstance(document.getElementById('accountModal')).hide();
            showToast('保存成功');
            loadAccounts();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 删除账户
 */
async function deleteAccount(id) {
    if (!confirm('确定要删除这个账户吗？')) return;
    
    try {
        const response = await apiRequest(`/accounts/${id}`, {
            method: 'DELETE'
        });
        
        if (response.success) {
            showToast('删除成功');
            loadAccounts();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 填充账户选择框
 */
function populateAccountSelect() {
    const select = document.getElementById('transactionAccount');
    select.innerHTML = accounts.map(a => 
        `<option value="${a.id}">${a.name}</option>`
    ).join('');
}

// ==================== 分类管理 ====================

/**
 * 加载分类列表
 */
async function loadCategories() {
    try {
        const response = await apiRequest('/categories');
        
        if (response.success) {
            categories = response.data;
            renderCategories();
        }
    } catch (error) {
        console.error('加载分类失败:', error);
    }
}

/**
 * 渲染分类列表
 */
function renderCategories() {
    const incomeCategories = categories.filter(c => c.type === 'INCOME');
    const expenseCategories = categories.filter(c => c.type === 'EXPENSE');
    
    document.getElementById('incomeCategories').innerHTML = renderCategoryList(incomeCategories);
    document.getElementById('expenseCategories').innerHTML = renderCategoryList(expenseCategories);
}

/**
 * 渲染分类列表项
 */
function renderCategoryList(cats) {
    if (!cats || cats.length === 0) {
        return '<div class="text-muted text-center py-3">暂无分类</div>';
    }
    
    return cats.map(c => `
        <div class="d-flex align-items-center justify-content-between p-2 border-bottom">
            <div class="d-flex align-items-center">
                <span class="category-tag" style="background: ${c.iconColor || '#667eea'}20; color: ${c.iconColor || '#667eea'}">
                    ${c.icon || '📁'} ${c.name}
                </span>
            </div>
            ${!c.isSystem ? `
                <div>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editCategory(${c.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteCategory(${c.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            ` : '<span class="badge bg-secondary">系统</span>'}
        </div>
    `).join('');
}

/**
 * 打开分类模态框
 */
function openCategoryModal() {
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryId').value = '';
    document.getElementById('categoryModalTitle').textContent = '新增分类';
    
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    modal.show();
}

/**
 * 编辑分类
 */
function editCategory(id) {
    const category = categories.find(c => c.id === id);
    if (!category) return;
    
    document.getElementById('categoryId').value = category.id;
    document.getElementById('categoryName').value = category.name;
    document.getElementById('categoryType').value = category.type;
    document.getElementById('categoryIcon').value = category.icon || '';
    document.getElementById('categoryColor').value = category.iconColor || '#667eea';
    document.getElementById('categoryModalTitle').textContent = '编辑分类';
    
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    modal.show();
}

/**
 * 保存分类
 */
async function saveCategory() {
    const id = document.getElementById('categoryId').value;
    const data = {
        name: document.getElementById('categoryName').value,
        type: document.getElementById('categoryType').value,
        icon: document.getElementById('categoryIcon').value,
        iconColor: document.getElementById('categoryColor').value
    };
    
    try {
        let response;
        if (id) {
            response = await apiRequest(`/categories/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        } else {
            response = await apiRequest('/categories', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }
        
        if (response.success) {
            bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
            showToast('保存成功');
            loadCategories();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 删除分类
 */
async function deleteCategory(id) {
    if (!confirm('确定要删除这个分类吗？')) return;
    
    try {
        const response = await apiRequest(`/categories/${id}`, {
            method: 'DELETE'
        });
        
        if (response.success) {
            showToast('删除成功');
            loadCategories();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 填充分类选择框
 */
function populateCategorySelect() {
    const type = document.getElementById('transactionType').value;
    const filteredCategories = categories.filter(c => c.type === type);
    
    const select = document.getElementById('transactionCategory');
    select.innerHTML = filteredCategories.map(c => 
        `<option value="${c.id}">${c.icon || ''} ${c.name}</option>`
    ).join('');
}

// 监听交易类型变化
document.getElementById('transactionType').addEventListener('change', populateCategorySelect);

/**
 * 获取分类名称
 */
function getCategoryName(categoryId) {
    const category = categories.find(c => c.id === categoryId);
    return category ? category.name : '未分类';
}

/**
 * 获取分类图标
 */
function getCategoryIcon(categoryId) {
    const category = categories.find(c => c.id === categoryId);
    return {
        icon: category?.icon || '📁',
        color: category?.iconColor || '#667eea'
    };
}

/**
 * 获取账户名称
 */
function getAccountName(accountId) {
    const account = accounts.find(a => a.id === accountId);
    return account ? account.name : '未知账户';
}

// ==================== 预算管理 ====================

/**
 * 加载预算列表
 */
async function loadBudgets() {
    try {
        const response = await apiRequest('/budgets');
        
        if (response.success) {
            renderBudgets(response.data);
        }
    } catch (error) {
        document.getElementById('budgetsList').innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="bi bi-piggy-bank"></i>
                    <h5>暂无预算</h5>
                    <p>点击新增按钮创建预算</p>
                </div>
            </div>
        `;
    }
}

/**
 * 渲染预算列表
 */
function renderBudgets(budgets) {
    if (!budgets || budgets.length === 0) {
        document.getElementById('budgetsList').innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="bi bi-piggy-bank"></i>
                    <h5>暂无预算</h5>
                    <p>点击新增按钮创建预算</p>
                </div>
            </div>
        `;
        return;
    }
    
    const periodNames = {
        'DAILY': '每日',
        'WEEKLY': '每周',
        'MONTHLY': '每月',
        'YEARLY': '每年'
    };
    
    let html = '';
    
    budgets.forEach(budget => {
        const percentage = (budget.spent / budget.amount * 100).toFixed(0);
        const progressClass = percentage >= 80 ? 'bg-danger' : percentage >= 50 ? 'bg-warning' : 'bg-success';
        
        html += `
            <div class="col-md-6 col-lg-4">
                <div class="card-container">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <h5 class="mb-1">${budget.name}</h5>
                            <small class="text-muted">${periodNames[budget.period] || budget.period}</small>
                        </div>
                        <div>
                            <button class="btn btn-sm btn-outline-primary me-1" onclick="editBudget(${budget.id})">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="deleteBudget(${budget.id})">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                    <div class="budget-item mb-0">
                        <div class="budget-header">
                            <span class="budget-amount">${formatMoney(budget.spent)} / ${formatMoney(budget.amount)}</span>
                            <span class="badge ${percentage >= 80 ? 'bg-danger' : 'bg-success'}">${percentage}%</span>
                        </div>
                        <div class="progress mt-2">
                            <div class="progress-bar ${progressClass}" style="width: ${Math.min(percentage, 100)}%"></div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });
    
    document.getElementById('budgetsList').innerHTML = html;
}

/**
 * 打开预算模态框
 */
function openBudgetModal() {
    document.getElementById('budgetForm').reset();
    document.getElementById('budgetId').value = '';
    document.getElementById('budgetModalTitle').textContent = '新增预算';
    
    // 填充分类选项
    const expenseCategories = categories.filter(c => c.type === 'EXPENSE');
    document.getElementById('budgetCategory').innerHTML = `
        <option value="">全部支出</option>
        ${expenseCategories.map(c => `<option value="${c.id}">${c.icon || ''} ${c.name}</option>`).join('')}
    `;
    
    const modal = new bootstrap.Modal(document.getElementById('budgetModal'));
    modal.show();
}

/**
 * 编辑预算
 */
async function editBudget(id) {
    try {
        const response = await apiRequest(`/budgets/${id}`);
        
        if (response.success) {
            const budget = response.data;
            
            document.getElementById('budgetId').value = budget.id;
            document.getElementById('budgetName').value = budget.name;
            document.getElementById('budgetAmount').value = budget.amount;
            document.getElementById('budgetPeriod').value = budget.period;
            document.getElementById('budgetThreshold').value = budget.alertThreshold;
            document.getElementById('budgetModalTitle').textContent = '编辑预算';
            
            // 填充分类选项
            const expenseCategories = categories.filter(c => c.type === 'EXPENSE');
            document.getElementById('budgetCategory').innerHTML = `
                <option value="">全部支出</option>
                ${expenseCategories.map(c => `<option value="${c.id}" ${c.id === budget.categoryId ? 'selected' : ''}>${c.icon || ''} ${c.name}</option>`).join('')}
            `;
            
            const modal = new bootstrap.Modal(document.getElementById('budgetModal'));
            modal.show();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 保存预算
 */
async function saveBudget() {
    const id = document.getElementById('budgetId').value;
    const categoryId = document.getElementById('budgetCategory').value;
    
    const data = {
        name: document.getElementById('budgetName').value,
        amount: parseFloat(document.getElementById('budgetAmount').value),
        period: document.getElementById('budgetPeriod').value,
        categoryId: categoryId ? parseInt(categoryId) : null,
        alertThreshold: parseInt(document.getElementById('budgetThreshold').value),
        isActive: true
    };
    
    try {
        let response;
        if (id) {
            response = await apiRequest(`/budgets/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        } else {
            response = await apiRequest('/budgets', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }
        
        if (response.success) {
            bootstrap.Modal.getInstance(document.getElementById('budgetModal')).hide();
            showToast('保存成功');
            loadBudgets();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 删除预算
 */
async function deleteBudget(id) {
    if (!confirm('确定要删除这个预算吗？')) return;
    
    try {
        const response = await apiRequest(`/budgets/${id}`, {
            method: 'DELETE'
        });
        
        if (response.success) {
            showToast('删除成功');
            loadBudgets();
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// ==================== 统计分析 ====================

let expenseChart = null;
let trendChart = null;

/**
 * 初始化统计页面
 */
function initStatistics() {
    const today = new Date();
    const startDate = new Date(today.getFullYear(), today.getMonth(), 1);
    const endDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    
    document.getElementById('statsStartDate').value = startDate.toISOString().split('T')[0];
    document.getElementById('statsEndDate').value = endDate.toISOString().split('T')[0];
    
    loadStatistics();
}

/**
 * 加载统计数据
 */
async function loadStatistics() {
    const startDate = document.getElementById('statsStartDate').value;
    const endDate = document.getElementById('statsEndDate').value;
    
    if (!startDate || !endDate) {
        showToast('请选择日期范围', 'error');
        return;
    }
    
    try {
        const response = await apiRequest(`/transactions/statistics?startDate=${startDate}&endDate=${endDate}`);
        
        if (response.success) {
            renderCharts(response.data);
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

/**
 * 渲染图表
 */
function renderCharts(stats) {
    // 支出分布饼图
    const expenseCtx = document.getElementById('expenseChart').getContext('2d');
    
    if (expenseChart) {
        expenseChart.destroy();
    }
    
    const categoryStats = stats.categoryStatistics || [];
    
    expenseChart = new Chart(expenseCtx, {
        type: 'doughnut',
        data: {
            labels: categoryStats.map(c => c.categoryName),
            datasets: [{
                data: categoryStats.map(c => c.amount),
                backgroundColor: categoryStats.map(c => c.categoryColor || '#667eea'),
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right'
                }
            }
        }
    });
    
    // 收支趋势折线图
    const trendCtx = document.getElementById('trendChart').getContext('2d');
    
    if (trendChart) {
        trendChart.destroy();
    }
    
    const trendData = stats.trendData || [];
    
    trendChart = new Chart(trendCtx, {
        type: 'line',
        data: {
            labels: trendData.map(t => t.date),
            datasets: [
                {
                    label: '收入',
                    data: trendData.map(t => t.income),
                    borderColor: '#38ef7d',
                    backgroundColor: 'rgba(56, 239, 125, 0.1)',
                    fill: true,
                    tension: 0.4
                },
                {
                    label: '支出',
                    data: trendData.map(t => t.expense),
                    borderColor: '#f45c43',
                    backgroundColor: 'rgba(244, 92, 67, 0.1)',
                    fill: true,
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// ==================== 初始化 ====================

/**
 * 初始化应用
 */
function init() {
    // 检查登录状态
    const savedUser = localStorage.getItem('user');
    if (authToken && savedUser) {
        currentUser = JSON.parse(savedUser);
        showApp();
    }
    
    // 设置默认日期
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('filterStartDate').value = today;
    document.getElementById('filterEndDate').value = today;
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', init);
