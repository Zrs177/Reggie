package com.example.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.OrderDto;
import com.example.reggie.common.R;
import com.example.reggie.mapper.OrdersMapper;
import com.example.reggie.pojo.*;
import com.example.reggie.service.*;
import com.example.reggie.util.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    @Resource
    private IShoppingCartService shoppingCartService;
    @Resource
    private IUserService userService;
    @Resource
    private IAddressBookService addressBookService;
    @Resource
    private IOrdersDetailService ordersDetailService;
    @Resource
    private IDishService dishService;
    @Resource
    private ISetmealService setmealService;
    @Transactional
    @Override
    public R submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            return R.error("购物车不能为空");
        }

        for(ShoppingCart shoppingCart:shoppingCarts){
            if (shoppingCart.getDishId()!=null){
                Integer status = dishService.getById(shoppingCart.getDishId()).getStatus();
                if (status!=1){
                    shoppingCartService.remove(wrapper);
                    return R.error("商品已下架");
                }
            }else {
                Integer status = setmealService.getById(shoppingCart.getSetmealId()).getStatus();
                if (status!=1){
                    shoppingCartService.remove(wrapper);
                    return R.error("商品已下架");
                }
            }
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            return R.error("地址有误");
        }

        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        ordersDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(wrapper);
        return R.success("下单成功");
    }

    @Override
    public R pages(Integer page, Integer pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime) {
        Long userId = BaseContext.getCurrentId();
        Page<Orders> pages=new Page<>(page,pageSize);
        QueryWrapper queryWrapper=new QueryWrapper<>();
        if (number!=null){
            queryWrapper.like("number",number);
        }
        if (beginTime!=null){
            queryWrapper.eq("order_time",beginTime);
            queryWrapper.eq("checkout_time",endTime);
        }
        page(pages,queryWrapper);
        return R.success(pages);
    }

    @Override
    @Transactional
    public R userPage(Integer page, Integer pageSize, HttpSession session) {
        Long userId = BaseContext.getCurrentId();
        Page userPage=new Page<>(page,pageSize);
        QueryWrapper queryWrapper=new QueryWrapper<>();
        List<Orders> orders = query().eq("user_id", userId).list();
        if (orders!=null){
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("order_time");
        page(userPage,queryWrapper);
        List<Orders> records = userPage.getRecords();
        List<OrderDto>orderDto=new ArrayList<>();
        for (Orders record:records){
            OrderDto dto=new OrderDto();
            BeanUtil.copyProperties(record,dto);
            List<OrderDetail> order_id = ordersDetailService.query().eq("order_id", record.getId()).list();
            dto.setOrderDetails(order_id);
            orderDto.add(dto);
        }
        userPage.setRecords(orderDto);
        return R.success(userPage);
    }

    @Override
    public R updateStatus(Orders orders) {
        Integer status = orders.getStatus();
        update().setSql("status="+status).eq("id",orders.getId()).update();
        return R.success("修改成功");
    }

    @Override
    public R again(Orders orders) {
//        Orders order = getById(orders.getId());
//        List<OrderDetail> orderDetails = ordersDetailService.query().eq("order_id", orders.getId()).list();
//        List<ShoppingCart>shoppingCarts=new ArrayList<>();
//        for (OrderDetail orderDetail:orderDetails){
//            ShoppingCart shoppingCart=new ShoppingCart();
//            shoppingCart.setId(RandomUtil.randomLong());
//            shoppingCart.setName(orderDetail.getName());
//            shoppingCart.setImage(orderDetail.getImage());
//            shoppingCart.setUserId(BaseContext.getCurrentId());
//            shoppingCart.setDishId(orderDetail.getDishId());
//            shoppingCart.setSetmealId(orderDetail.getSetmealId());
//            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
//            shoppingCart.setNumber(orderDetail.getNumber());
//            shoppingCart.setAmount(orderDetail.getAmount());
////             BeanUtil.copyProperties(orderDetail,shoppingCart);
//             shoppingCart.setCreateTime(LocalDateTime.now());
//             shoppingCarts.add(shoppingCart);
//        }
        return R.success("再来一单");
    }
}
