package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.pojo.User;
import com.example.reggie.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;
    @PostMapping("/sendMsg")
    private R sendMsg(@RequestBody User user, HttpSession session){
        return userService.sendMsg(user,session);
    }
    @PostMapping("/login")
    private R login(@RequestBody Map map, HttpSession session){
        return userService.login(map,session);
    }
}
