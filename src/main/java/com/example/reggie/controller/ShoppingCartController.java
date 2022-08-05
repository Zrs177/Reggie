package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.pojo.ShoppingCart;
import com.example.reggie.service.IShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
      @Resource
      private IShoppingCartService shoppingCartService;
      @GetMapping("/list")
      public R getList(){
          return shoppingCartService.getList();
      }
      @PostMapping("/add")
      public R addToCar(@RequestBody ShoppingCart shoppingCart){
         return shoppingCartService.addToCar(shoppingCart);
      }
      @PostMapping("/sub")
      public R subCar(@RequestBody ShoppingCart shoppingCart){
          return shoppingCartService.subCar(shoppingCart);
      }
      @DeleteMapping("/clean")
      public R clean(){
          return shoppingCartService.deleteAll();
      }
}
