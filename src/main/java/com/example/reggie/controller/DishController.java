package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Dish;
import com.example.reggie.service.IDishService;
import com.example.reggie.service.impl.CategoryServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private IDishService dishService;

    @Resource
    private CategoryServiceImpl categoryService;
    @GetMapping("/page")
    private R pageAll(Integer page,Integer pageSize,String name){
        return dishService.pageAll(page,pageSize,name);
    }
    @PostMapping
    private R saveDish(@RequestBody DishDto dishDto){
        return dishService.saveDish(dishDto);
    }
    //让信息回显到页面
    @GetMapping("/{id}")
    private R appearById(@PathVariable("id")Long id){
        return dishService.appearById(id);
    }
    @PutMapping
    private R update(@RequestBody DishDto dishDto){
       return dishService.updateDto(dishDto);
    }
    @DeleteMapping
    private R deleteById(Long[] ids){
        return dishService.deleteById(ids);
    }
    @PostMapping("/status/{status}")
    private R stopBatchSell(@PathVariable("status") Integer status,Long[] ids){
        return dishService.stopBatchSell(status,ids);
    }
    @GetMapping("/list")
    private R getCategoryById(Dish dish){
        return dishService.getCategoryById(dish);
    }
}
