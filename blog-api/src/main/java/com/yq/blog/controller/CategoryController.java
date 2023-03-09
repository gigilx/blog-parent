package com.yq.blog.controller;

import com.yq.blog.service.CategoryService;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    //获取文章目录
    @GetMapping
    public Result categories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("detail")
    public Result categoriesDetails(){
        return categoryService.getAllCategoriesDetails();
    }

    @GetMapping("detail/{id}")
    public Result categoryDetails(@PathVariable("id") long id){
        return categoryService.getCategoryDetailsByCategoryId(id);
    }
}
