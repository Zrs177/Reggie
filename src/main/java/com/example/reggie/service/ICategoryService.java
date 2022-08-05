package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Category;

import java.util.List;

public interface ICategoryService extends IService<Category> {
    R pageAll(int page, int pageSize);



    R saveDishCategory(Category category);

    R updateCategory(Category category);

    R listall(Category category);
}
