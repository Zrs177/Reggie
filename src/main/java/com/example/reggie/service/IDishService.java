package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Dish;

public interface IDishService extends IService<Dish> {

    R saveDish(DishDto dishDto);

    R appearById(Long id);


    R deleteById(Long[] ids);

    R stopBatchSell(Integer status, Long[] ids);

    R getCategoryById(Dish dish);

    R pageAll(Integer page, Integer pageSize, String name);

    R updateDto(DishDto dishDto);
}
