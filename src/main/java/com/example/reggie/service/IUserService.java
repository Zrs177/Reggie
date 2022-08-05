package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface IUserService extends IService<User> {
    R sendMsg(User user, HttpSession session);

    R login(Map map, HttpSession session);
}
