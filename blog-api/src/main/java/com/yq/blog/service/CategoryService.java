package com.yq.blog.service;

import com.yq.blog.dao.pojo.Category;
import com.yq.blog.vo.CategoryVo;
import com.yq.blog.vo.params.Result;

public interface CategoryService {

    /**
     * 查询类别
     * @param categoryId
     * @return
     */
    CategoryVo findCategoryById(long categoryId);

    /**
     * 查询所有类别
     * @return
     */
    Result getAllCategories();

    /**
     * 类别细节
     * @return
     */
    Result getAllCategoriesDetails();

    /**
     * 单个类别
     * @param id
     * @return
     */
    Result getCategoryDetailsByCategoryId(long categoryId);
}
