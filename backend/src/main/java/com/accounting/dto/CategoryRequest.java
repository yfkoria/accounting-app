package com.accounting.dto;

import com.accounting.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 分类请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    @NotNull(message = "分类类型不能为空")
    private Category.CategoryType type;
    
    private Long parentId;
    private String icon;
    private String iconColor;
    private String description;
    private Integer sortOrder;
}
