package com.accounting.controller;

import com.accounting.dto.ApiResponse;
import com.accounting.dto.AuthResponse;
import com.accounting.dto.LoginRequest;
import com.accounting.dto.RegisterRequest;
import com.accounting.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录等认证相关接口")
// 👇 只加这一行！解决跨域，前端100%能连上
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账户")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success("注册成功", response);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取JWT令牌")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success("登录成功", response);
    }
}