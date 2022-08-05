package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Orders;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

public interface IOrdersService extends IService<Orders> {
    R submit(Orders orders);

    R pages(Integer page, Integer pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime);

    R userPage(Integer page, Integer pageSize, HttpSession session);

    R updateStatus(Orders orders);

    R again(Orders orders);
}
