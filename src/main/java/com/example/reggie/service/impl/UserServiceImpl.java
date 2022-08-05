package com.example.reggie.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.pojo.User;
import com.example.reggie.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public R sendMsg(User user, HttpSession session) {
        if (user==null){
            return R.error("手机号为空");
        }
        String phone = user.getPhone();
        String code = RandomUtil.randomNumbers(4);
//        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info(code);
//        session.setAttribute(phone,code);
        stringRedisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
        return R.success("手机验证码发送成功");
    }

    @Override
    public R login(Map map, HttpSession session) {
        if (map!=null){
            String phone = map.get("phone").toString();
            String code = map.get("code").toString();
            String coded = stringRedisTemplate.opsForValue().get(phone);
            if (!code.equals(coded)) {
                return R.error("验证码错误");
            }
            stringRedisTemplate.delete(phone);
            User user = query().eq("phone", phone).one();
            if (user==null){
                user=new User();
                user.setPhone(phone);
                String name="user_"+RandomUtil.randomNumbers(6);
                user.setName(name);
                save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}

