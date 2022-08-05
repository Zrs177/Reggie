package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.ShoppingCart;

public interface IShoppingCartService extends IService<ShoppingCart> {
    R getList();

    R addToCar(ShoppingCart shoppingCart);

    R subCar(ShoppingCart shoppingCart);

    R deleteAll();
}
