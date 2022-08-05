package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.pojo.Category;
import com.example.reggie.service.ICategoryService;
import com.example.reggie.service.IDishService;
import com.example.reggie.service.ISetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Override
    public R pageAll(int page, int pageSize) {
        IPage data=new Page(page,pageSize);
        QueryWrapper<Category> condition=new QueryWrapper<>();
        condition.orderByAsc("sort");
        page(data,condition);
        return R.success(data);
    }



    @Override
    public R saveDishCategory(Category category) {
        boolean save = save(category);
        return R.success(save);
    }

    @Override
    public R updateCategory(Category category) {
        boolean success = updateById(category);
        if (!success){
            return R.error("更新失败");
        }
        return R.success("更新成功");
    }

    @Override
    public R listall(Category category) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        if(category.getType()!=null){
            queryWrapper.eq("type", category.getType());
        }
        queryWrapper.orderByAsc("sort").orderByDesc("update_time");
        List<Category> list = list(queryWrapper);
        return R.success(list);
    }
}
