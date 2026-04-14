package com.accounting.controller;

import com.accounting.dto.ApiResponse;
import com.accounting.dto.CategoryRequest;
import com.accounting.entity.Category;
import com.accounting.service.AuthService;
import com.accounting.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 * 处理分类管理相关请求
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "分类的增删改查接口")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final AuthService authService;
    
    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "创建新的自定义分类")
    public ApiResponse<Category> createCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CategoryRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Category category = categoryService.createCategory(userId, request);
        return ApiResponse.success("分类创建成功", category);
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新分类信息")
    public ApiResponse<Category> updateCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Category category = categoryService.updateCategory(userId, id, request);
        return ApiResponse.success("分类更新成功", category);
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除指定分类")
    public ApiResponse<Void> deleteCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        categoryService.deleteCategory(userId, id);
        return ApiResponse.success("分类删除成功", null);
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping
    @Operation(summary = "获取所有分类", description = "获取当前用户的所有分类（包含系统分类）")
    public ApiResponse<List<Category>> getAllCategories(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        List<Category> categories = categoryService.getUserCategories(userId);
        return ApiResponse.success(categories);
    }
    
    /**
     * 按类型获取分类
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取分类", description = "获取指定类型的分类")
    public ApiResponse<List<Category>> getCategoriesByType(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Category.CategoryType type) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        List<Category> categories = categoryService.getCategoriesByType(userId, type);
        return ApiResponse.success(categories);
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "获取指定分类的详细信息")
    public ApiResponse<Category> getCategoryById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserByUsername(userDetails.getUsername()).getId();
        Category category = categoryService.getCategoryById(userId, id);
        return ApiResponse.success(category);
    }
}
