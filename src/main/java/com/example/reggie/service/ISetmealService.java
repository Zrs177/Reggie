package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Setmeal;

public interface ISetmealService extends IService<Setmeal> {
    R saveSetmeal(SetmealDto setmealDto);

    R PageAll(Integer page, Integer pageSize, String name);

    R deleteById(Long[] ids);

    R stopBatchSell(Integer status, Long[] ids);

    R appearById(Long id);

    R Update(SetmealDto setmealDto);

    R setmealList(Setmeal setmeal);
}
