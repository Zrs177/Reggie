package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.pojo.Employee;
import com.example.reggie.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements IEmployeeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public R login(HttpServletRequest request, Employee employee) {
        String password = employee.getPassword();
        Employee employee1 = query().eq("username", employee.getUsername()).one();
        if (employee1==null){
            return R.error("用户名不存在");
        }
        if (!employee1.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (employee1.getStatus()==0){
            return R.error("该账号已禁用");
        }
        request.getSession().setAttribute("employee",employee1.getId());
//        stringRedisTemplate.opsForValue().set("employee",employee1.getId().toString());
        return R.success(employee1);
    }

    @Override
    public R logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @Override
    public R pageAll(Integer page, Integer pageSize, String name) {
        IPage data=new Page(page,pageSize);
        if (name==null){
//        QueryWrapper<Employee> condition=new QueryWrapper<>();
            page(data);
            return R.success(data);
        }else {
            QueryWrapper<Employee> condition=new QueryWrapper<>();
            condition.like("name",name);
            page(data,condition);
            return R.success(data);
        }

    }

    @Override
    public R saveEmployee(Employee employee) {
        if(employee==null){
            return R.error("员工不存在");
        }
        Integer user = query().eq("username", employee.getUsername()).count();
        if (user>0){
            return R.error("用户名不可重复");
        }
        boolean save = save(employee);
        return R.success("新增员工成功");
    }

    @Override
    public R UpdateStatusById(Employee employee) {
        boolean success = updateById(employee);
        return R.success("员工信息修改成功");
    }

    @Override
    public R UpdateById(Long id) {
        if (id!=null){
            Employee employee = query().eq("id", id).one();
            return R.success(employee);
        }
        return R.error("员工ID为空值");
    }
}
