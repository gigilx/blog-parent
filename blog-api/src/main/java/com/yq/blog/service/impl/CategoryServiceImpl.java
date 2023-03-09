package com.yq.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yq.blog.dao.mapper.CategoryMapper;
import com.yq.blog.dao.pojo.Category;
import com.yq.blog.service.CategoryService;
import com.yq.blog.vo.CategoryVo;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public CategoryVo findCategoryById(long categoryId) {
        CategoryVo categoryVo = new CategoryVo();
        Category category = categoryMapper.selectById(categoryId);
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }

    @Override
    public Result getAllCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Category::getId,Category::getCategoryName);
        List<CategoryVo> categoryVoList = copyList(categoryMapper.selectList(queryWrapper));

        return Result.success(categoryVoList);
    }

    @Override
    public Result getAllCategoriesDetails() {
        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(categoryList);
    }

    @Override
    public Result getCategoryDetailsByCategoryId(long categoryId) {

        return Result.success(categoryMapper.selectById(categoryId));
    }

    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo>categoryVoList = new ArrayList<>();
        for(Category category : categoryList){
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }
}
