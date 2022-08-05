package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.mapper.OrderDetailMapper;
import com.example.reggie.pojo.OrderDetail;
import com.example.reggie.service.IOrdersDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrdersDetailService {
}
