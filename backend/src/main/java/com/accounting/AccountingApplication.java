package com.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 记账应用主启动类
 * 
 * @author Accounting App Team
 * @version 1.0.0
 */
@SpringBootApplication
public class AccountingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AccountingApplication.class, args);
        System.out.println("====================================");
        System.out.println("   记账应用启动成功！");
        System.out.println("   API文档: http://localhost:8080/api/swagger-ui.html");
        System.out.println("   H2控制台: http://localhost:8080/api/h2-console");
        System.out.println("====================================");
    }
}
