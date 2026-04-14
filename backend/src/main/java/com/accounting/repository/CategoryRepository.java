package com.accounting.repository;

import com.accounting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 分类数据访问层
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据用户ID查找所有分类
     */
    List<Category> findByUserIdOrIsSystemTrueOrderBySortOrderAsc(Long userId);
    
    /**
     * 根据用户ID和类型查找分类
     */
    List<Category> findByUserIdOrIsSystemTrueAndTypeOrderBySortOrderAsc(Long userId, Category.CategoryType type);
    
    /**
     * 根据用户ID和分类ID查找分类
     */
    Optional<Category> findByIdAndUserIdOrIsSystemTrue(Long id, Long userId);
    
    /**
     * 查找子分类
     */
    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    /**
     * 检查分类名称是否存在
     */
    boolean existsByUserIdAndNameAndType(Long userId, String name, Category.CategoryType type);
    
    /**
     * 查找系统分类
     */
    List<Category> findByIsSystemTrueOrderBySortOrderAsc();
}
