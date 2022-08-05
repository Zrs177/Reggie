package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.Employee;

import javax.servlet.http.HttpServletRequest;

public interface IEmployeeService extends IService<Employee> {
    R login(HttpServletRequest request, Employee employee);

    R logout(HttpServletRequest request);

    R pageAll(Integer page, Integer pageSize, String name);

    R saveEmployee(Employee employee);

    R UpdateStatusById(Employee employee);

    R UpdateById(Long id);
}
