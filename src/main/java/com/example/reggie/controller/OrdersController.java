package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.pojo.Orders;
import com.example.reggie.service.IOrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Resource
    private IOrdersService ordersService;
    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders){
         return ordersService.submit(orders);
    }
    @GetMapping("/page")
    public R page(Integer page, Integer pageSize, Long number, LocalDateTime beginTime,LocalDateTime endTime){
        return ordersService.pages(page,pageSize,number,beginTime,endTime);
    }
    @GetMapping("/userPage")
    public R userPage(Integer page,Integer pageSize, HttpSession session){
        return ordersService.userPage(page,pageSize, session);
    }
    @PutMapping
    public R updateStatus(@RequestBody Orders orders){
        return ordersService.updateStatus(orders);
    }
    @PostMapping("/again")
    public R again(@RequestBody Orders orders){
        return ordersService.again(orders);
    }
}
