package com.example.reggie.DTO;

import com.example.reggie.pojo.OrderDetail;
import com.example.reggie.pojo.Orders;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
