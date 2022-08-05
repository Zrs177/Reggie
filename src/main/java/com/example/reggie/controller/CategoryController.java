package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import com.example.reggie.service.ICategoryService;
import com.example.reggie.service.IDIshFlavorService;
import com.example.reggie.service.IDishService;
import com.example.reggie.service.ISetmealService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private ICategoryService categoryService;
    @Resource
    private IDishService dishService;
    @Resource
    private ISetmealService setmealService;
    @Resource
    private IDIshFlavorService idIshFlavorService;
    @Resource
    private ICategoryService category;
    @GetMapping("/page")
    private R pageAll(int page,int pageSize){
        return category.pageAll(page,pageSize);
    }
    @DeleteMapping
    private R deleteById(Long ids){
        if (dishService.query().eq("category_id",ids).count()>0) {
            return R.error("当前分类关联到某些菜品");
        }
        if(setmealService.query().eq("category_id",ids).count()>0){
            return R.error("当前分类关联到某些套餐");
        }
        categoryService.removeById(ids);
        return R.success("删除成功");
    }
    @PostMapping
    private R saveDishCategory(@RequestBody Category category){
       return categoryService.saveDishCategory(category);
    }
    @PutMapping
    public R updateCategory(@RequestBody Category category){
        return categoryService.updateCategory(category);
    }
    @GetMapping("/list")
    public R list(Category category) {
           return categoryService.listall(category);
    }

    }

