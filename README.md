# 智能记账 - 个人财务管理助手

## 项目概述

智能记账是一款功能完善的个人财务管理应用，采用前后端分离架构设计，后端使用 Spring Boot 框架开发，前端采用现代化的 HTML5 + CSS3 + JavaScript 技术栈构建。该应用旨在帮助用户轻松管理日常收支、追踪账户余额、制定预算计划，并通过直观的可视化图表分析财务状况。

### 核心特性

- **用户认证系统**：支持用户注册、登录，采用 JWT 令牌进行身份验证，确保用户数据安全
- **多账户管理**：支持现金、银行卡、信用卡、支付宝、微信等多种账户类型，方便统一管理各类资金
- **智能分类**：内置常用收支分类，支持用户自定义分类，让记账更加便捷
- **交易记录**：完整记录每笔收入和支出，支持按日期范围筛选查询
- **预算管理**：可设置日/周/月/年度预算，实时追踪支出进度，超支预警提醒
- **统计分析**：通过饼图、折线图等可视化方式展示收支分布和趋势变化
- **响应式设计**：适配桌面端和移动端，随时随地管理财务

---

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架，简化 Spring 应用开发 |
| Spring Data JPA | 3.2.0 | ORM 框架，简化数据库操作 |
| Spring Security | 3.2.0 | 安全框架，提供认证和授权 |
| H2 Database | 2.2.224 | 嵌入式数据库，无需额外安装 |
| JWT (jjwt) | 0.12.3 | JSON Web Token 实现，用于无状态认证 |
| Lombok | 1.18.30 | 代码简化工具，减少样板代码 |
| SpringDoc OpenAPI | 2.3.0 | API 文档生成工具 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| HTML5 | - | 页面结构 |
| CSS3 | - | 样式设计，包含渐变、动画等现代特性 |
| JavaScript (ES6+) | - | 交互逻辑 |
| Bootstrap | 5.3.2 | UI 框架，提供响应式布局和组件 |
| Bootstrap Icons | 1.11.1 | 图标库 |
| Chart.js | 4.x | 图表可视化库 |

---

## 项目结构

```
accounting-app/
├── backend/                          # 后端项目
│   ├── pom.xml                       # Maven 配置文件
│   └── src/
│       ├── main/
│       │   ├── java/com/accounting/
│       │   │   ├── AccountingApplication.java    # 主启动类
│       │   │   ├── config/                       # 配置类
│       │   │   │   ├── SecurityConfig.java       # 安全配置
│       │   │   │   ├── JwtAuthenticationFilter.java  # JWT过滤器
│       │   │   │   └── CustomUserDetailsService.java # 用户详情服务
│       │   │   ├── controller/                   # 控制器层
│       │   │   │   ├── AuthController.java       # 认证控制器
│       │   │   │   ├── AccountController.java    # 账户控制器
│       │   │   │   ├── CategoryController.java   # 分类控制器
│       │   │   │   ├── TransactionController.java # 交易控制器
│       │   │   │   └── BudgetController.java     # 预算控制器
│       │   │   ├── service/                      # 服务层
│       │   │   │   ├── AuthService.java          # 认证服务
│       │   │   │   ├── JwtService.java           # JWT服务
│       │   │   │   ├── AccountService.java       # 账户服务
│       │   │   │   ├── CategoryService.java      # 分类服务
│       │   │   │   ├── TransactionService.java   # 交易服务
│       │   │   │   └── BudgetService.java        # 预算服务
│       │   │   ├── repository/                   # 数据访问层
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── AccountRepository.java
│       │   │   │   ├── CategoryRepository.java
│       │   │   │   ├── TransactionRepository.java
│       │   │   │   └── BudgetRepository.java
│       │   │   ├── entity/                       # 实体类
│       │   │   │   ├── User.java
│       │   │   │   ├── Account.java
│       │   │   │   ├── Category.java
│       │   │   │   ├── Transaction.java
│       │   │   │   └── Budget.java
│       │   │   ├── dto/                          # 数据传输对象
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── AuthResponse.java
│       │   │   │   ├── AccountRequest.java
│       │   │   │   ├── CategoryRequest.java
│       │   │   │   ├── TransactionRequest.java
│       │   │   │   ├── BudgetRequest.java
│       │   │   │   ├── StatisticsDTO.java
│       │   │   │   └── ApiResponse.java
│       │   │   └── exception/                    # 异常处理
│       │   │       └── GlobalExceptionHandler.java
│       │   └── resources/
│       │       └── application.yml               # 应用配置
│       └── test/                                 # 测试目录
│
└── frontend/                         # 前端项目
    ├── index.html                    # 主页面
    └── app.js                        # 应用逻辑
```

---

## 快速开始

### 环境要求

- **JDK 17+**：Java 开发环境
- **Maven 3.6+**：项目构建工具
- **现代浏览器**：Chrome、Firefox、Edge 等

### 后端启动步骤

1. **进入后端目录**
   ```bash
   cd backend
   ```

2. **编译项目**
   ```bash
   mvn clean install -DskipTests
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run
   ```
   
   或者直接运行打包后的 JAR 文件：
   ```bash
   java -jar target/accounting-app-1.0.0.jar
   ```

4. **验证启动成功**
   
   启动成功后，控制台将显示：
   ```
   ====================================
      记账应用启动成功！
      API文档: http://localhost:8080/api/swagger-ui.html
      H2控制台: http://localhost:8080/api/h2-console
   ====================================
   ```

### 前端启动步骤

1. **打开前端页面**
   
   直接在浏览器中打开 `frontend/index.html` 文件，或者使用本地服务器：
   ```bash
   # 使用 Python 简易服务器
   cd frontend
   python -m http.server 3000
   
   # 或使用 Node.js 的 http-server
   npx http-server -p 3000
   ```

2. **访问应用**
   
   浏览器访问 `http://localhost:3000`

### 配置说明

后端配置文件位于 `src/main/resources/application.yml`，主要配置项：

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # API 上下文路径

spring:
  datasource:
    url: jdbc:h2:file:./data/accounting  # 数据库文件路径
    username: sa
    password: 

jwt:
  secret: YourSuperSecretKey... # JWT 密钥（生产环境请修改）
  expiration: 86400000          # 令牌有效期（毫秒）
```

---

## API 接口文档

### 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: Bearer Token (JWT)
- **数据格式**: JSON

### 接口列表

#### 1. 认证接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /auth/register | 用户注册 | 否 |
| POST | /auth/login | 用户登录 | 否 |

**注册请求示例**:
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "nickname": "测试用户"
}
```

**登录响应示例**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户"
    }
  }
}
```

#### 2. 账户接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /accounts | 获取所有账户 | 是 |
| GET | /accounts/{id} | 获取账户详情 | 是 |
| POST | /accounts | 创建账户 | 是 |
| PUT | /accounts/{id} | 更新账户 | 是 |
| DELETE | /accounts/{id} | 删除账户 | 是 |
| GET | /accounts/total-assets | 获取总资产 | 是 |

**创建账户请求示例**:
```json
{
  "name": "招商银行",
  "type": "BANK_CARD",
  "balance": 10000.00,
  "description": "工资卡"
}
```

#### 3. 分类接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /categories | 获取所有分类 | 是 |
| GET | /categories/type/{type} | 按类型获取分类 | 是 |
| POST | /categories | 创建分类 | 是 |
| PUT | /categories/{id} | 更新分类 | 是 |
| DELETE | /categories/{id} | 删除分类 | 是 |

#### 4. 交易接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /transactions | 分页获取交易 | 是 |
| GET | /transactions/{id} | 获取交易详情 | 是 |
| GET | /transactions/range | 按日期范围查询 | 是 |
| POST | /transactions | 创建交易 | 是 |
| PUT | /transactions/{id} | 更新交易 | 是 |
| DELETE | /transactions/{id} | 删除交易 | 是 |
| GET | /transactions/statistics | 获取统计数据 | 是 |

**创建交易请求示例**:
```json
{
  "type": "EXPENSE",
  "amount": 35.50,
  "accountId": 1,
  "categoryId": 2,
  "transactionDate": "2024-01-15",
  "remark": "午餐"
}
```

#### 5. 预算接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /budgets | 获取所有预算 | 是 |
| GET | /budgets/{id} | 获取预算详情 | 是 |
| GET | /budgets/alerts | 获取预算预警 | 是 |
| POST | /budgets | 创建预算 | 是 |
| PUT | /budgets/{id} | 更新预算 | 是 |
| DELETE | /budgets/{id} | 删除预算 | 是 |

---

## 功能模块详解

### 1. 用户认证模块

用户认证模块采用 JWT (JSON Web Token) 实现无状态认证。用户登录成功后，服务器返回一个 JWT 令牌，前端将其存储在 localStorage 中，后续请求通过 Authorization 请求头携带该令牌进行身份验证。

**安全特性**:
- 密码采用 BCrypt 加密存储
- JWT 令牌包含过期时间
- 支持用户角色权限控制

### 2. 账户管理模块

支持管理多种类型的账户，包括：

| 账户类型 | 说明 |
|----------|------|
| CASH | 现金账户 |
| BANK_CARD | 银行卡账户 |
| CREDIT_CARD | 信用卡账户 |
| ALIPAY | 支付宝账户 |
| WECHAT | 微信账户 |
| INVESTMENT | 投资账户 |
| OTHER | 其他账户 |

每个账户记录当前余额，交易发生时自动更新余额。

### 3. 分类管理模块

系统内置常用分类，用户也可自定义分类：

**内置支出分类**:
- 餐饮、交通、购物、娱乐、居住、通讯、医疗、教育、其他

**内置收入分类**:
- 工资、奖金、投资收益、兼职、红包、其他

### 4. 交易记录模块

记录每一笔收支交易，包含以下信息：
- 交易类型（收入/支出）
- 交易金额
- 关联账户
- 关联分类
- 交易日期
- 备注信息

支持按日期范围筛选查询，方便查看特定时间段的收支情况。

### 5. 预算管理模块

帮助用户控制支出，可设置：
- 预算名称
- 预算金额
- 预算周期（日/周/月/年）
- 关联分类（可选）
- 预警阈值

系统会实时计算已支出金额，当支出达到预警阈值时提醒用户。

### 6. 统计分析模块

提供直观的数据可视化：
- **支出分布饼图**：展示各分类支出占比
- **收支趋势折线图**：展示一段时间内的收支变化趋势

---

## 数据库设计

### ER 图关系

```
User (用户)
  ├── 1:N ── Account (账户)
  ├── 1:N ── Category (分类，用户自定义)
  ├── 1:N ── Transaction (交易记录)
  └── 1:N ── Budget (预算)

Account (账户)
  └── 1:N ── Transaction (交易记录)

Category (分类)
  └── 1:N ── Transaction (交易记录)
```

### 表结构说明

#### users 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(255) | 密码（加密） |
| email | VARCHAR(100) | 邮箱 |
| nickname | VARCHAR(50) | 昵称 |
| role | VARCHAR(20) | 角色 |
| enabled | BOOLEAN | 是否启用 |

#### accounts 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| name | VARCHAR(100) | 账户名称 |
| type | VARCHAR(30) | 账户类型 |
| balance | DECIMAL(19,2) | 余额 |
| description | VARCHAR(500) | 描述 |

#### categories 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID（系统分类为空） |
| name | VARCHAR(50) | 分类名称 |
| type | VARCHAR(20) | 分类类型 |
| icon | VARCHAR(50) | 图标 |
| icon_color | VARCHAR(20) | 颜色 |
| is_system | BOOLEAN | 是否系统分类 |

#### transactions 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| account_id | BIGINT | 账户ID |
| category_id | BIGINT | 分类ID |
| type | VARCHAR(20) | 交易类型 |
| amount | DECIMAL(19,2) | 金额 |
| transaction_date | DATE | 交易日期 |
| remark | VARCHAR(500) | 备注 |

#### budgets 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| category_id | BIGINT | 分类ID |
| name | VARCHAR(100) | 预算名称 |
| period | VARCHAR(20) | 预算周期 |
| amount | DECIMAL(19,2) | 预算金额 |
| spent | DECIMAL(19,2) | 已支出金额 |
| alert_threshold | INT | 预警阈值 |

---

## 部署指南

### 开发环境部署

直接使用 Maven 运行：
```bash
mvn spring-boot:run
```

### 生产环境部署

1. **打包应用**
   ```bash
   mvn clean package -DskipTests
   ```

2. **运行 JAR 包**
   ```bash
   java -jar target/accounting-app-1.0.0.jar
   ```

3. **后台运行**
   ```bash
   nohup java -jar accounting-app-1.0.0.jar > app.log 2>&1 &
   ```

### Docker 部署（可选）

创建 Dockerfile：
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/accounting-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建并运行：
```bash
docker build -t accounting-app .
docker run -d -p 8080:8080 accounting-app
```

---

## 常见问题

### Q1: 忘记密码怎么办？

当前版本暂不支持密码找回功能，建议通过 H2 控制台直接修改数据库中的密码字段（需使用 BCrypt 加密）。

### Q2: 如何切换到 MySQL 数据库？

修改 `application.yml` 配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/accounting
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
```

并添加 MySQL 依赖到 `pom.xml`：
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

### Q3: 前端无法连接后端 API？

检查以下几点：
1. 后端是否正常启动
2. CORS 配置是否正确
3. 前端 `API_BASE_URL` 配置是否正确

### Q4: 如何查看 API 文档？

启动后端后访问：`http://localhost:8080/api/swagger-ui.html`

---

## 版本历史

### v1.0.0 (2024-01)
- 初始版本发布
- 实现用户认证功能
- 实现账户管理功能
- 实现分类管理功能
- 实现交易记录功能
- 实现预算管理功能
- 实现统计分析功能

---

## 技术支持

如有问题或建议，欢迎通过以下方式联系：

- 项目地址：`accounting-app/`
- API 文档：`http://localhost:8080/api/swagger-ui.html`
- H2 控制台：`http://localhost:8080/api/h2-console`

---

## 许可证

本项目仅供学习和参考使用。
