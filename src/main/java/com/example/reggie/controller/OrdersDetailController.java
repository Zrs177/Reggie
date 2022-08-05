package com.example.reggie.controller;

import com.example.reggie.service.IOrdersDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orderDetail")
public class OrdersDetailController {
    @Resource
    private IOrdersDetailService ordersDetailService;
}
