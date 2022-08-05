package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.pojo.ShoppingCart;
import com.example.reggie.service.IShoppingCartService;
import com.example.reggie.util.BaseContext;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
    @Override
    public R getList() {
        List<ShoppingCart> shoppingCarts = query().eq("user_id", BaseContext.getCurrentId()).list();
        return R.success(shoppingCarts);
    }

    @Override
    public R addToCar(ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId!=null ) {
            ShoppingCart one = query().eq("user_id", userId).eq("dish_id", dishId).one();
            if (one!=null){
                one.setNumber(one.getNumber()+1);
                updateById(one);
            } else {
                shoppingCart.setNumber(1);
                save(shoppingCart);
                one=shoppingCart;
            }
            return R.success(one);
        }else {
            ShoppingCart one = query().eq("user_id", userId).eq("setmeal_id",setmealId).one();
            if (one!=null){
                one.setNumber(one.getNumber()+1);
                updateById(one);
            } else {
                shoppingCart.setNumber(1);
                save(shoppingCart);
                one=shoppingCart;
            }
            return R.success(one);
        }
    }

    @Override
    public R subCar(ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId !=null){
            ShoppingCart one = query().eq("user_id", userId).eq("dish_id", dishId).one();
            if (one.getNumber()>1){
                one.setNumber(one.getNumber()-1);
                updateById(one);
                return R.success(one);
            }
            removeById(one);
            return R.success("删除成功");
        }
        ShoppingCart one = query().eq("user_id", userId).eq("setmeal_id",setmealId).one();
        if (one.getNumber()>1){
            one.setNumber(one.getNumber()-1);
            updateById(one);
            return R.success(one);
        }
        removeById(one);
        return R.success("删除成功");
    }

    @Override
    public R deleteAll() {
        List<ShoppingCart> shoppingCarts = query().eq("user_id", BaseContext.getCurrentId()).list();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            removeById(shoppingCart.getId());
        }
        return R.success("购物车已清空");
    }
}
