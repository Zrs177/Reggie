package com.example.reggie.controller;

import com.example.reggie.DTO.DishDto;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.service.ISetmealService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private ISetmealService setmealService;
    @PostMapping
    private R saveSetmeal(@RequestBody SetmealDto setmealDto){
        return setmealService.saveSetmeal(setmealDto);
    }
    @GetMapping("/page")
    private R PageAll(Integer page,Integer pageSize,String name){
        return setmealService.PageAll(page,pageSize,name);
    }
    @DeleteMapping
    private R deleteById(Long[] ids){
        return setmealService.deleteById(ids);
    }
    @PostMapping("/status/{status}")
    private R stopBatchSell(@PathVariable("status") Integer status,Long[] ids){
        return setmealService.stopBatchSell(status,ids);
    }
    @GetMapping("/{id}")
    private R appearById(@PathVariable("id")Long id){
        return setmealService.appearById(id);
    }
    @PutMapping
    private R update(@RequestBody SetmealDto setmealDto){
        return setmealService.Update(setmealDto);
    }
    @GetMapping("/list")
    private R setmealList(Setmeal setmeal){
        return setmealService.setmealList(setmeal);
    }
}
