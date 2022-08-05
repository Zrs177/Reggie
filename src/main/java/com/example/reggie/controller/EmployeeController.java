package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.pojo.Employee;
import com.example.reggie.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private IEmployeeService employeeService;
    @PostMapping("/login")
    public R login(@RequestBody Employee employee, HttpServletRequest request){
        return employeeService.login(request,employee);
    }
    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        return employeeService.logout(request);
    }
    @GetMapping("/page")
    public R pageAll(Integer page,Integer pageSize,String name){
        return employeeService.pageAll(page,pageSize,name);
    }
    @PostMapping
    public R saveEmployee(@RequestBody Employee employee){
        return employeeService.saveEmployee(employee);
    }
    @PutMapping
    public R UpdateStatusById(@RequestBody Employee employee){
        return employeeService.UpdateStatusById(employee);
    }
    @GetMapping("/{id}")
    public R updateById(@PathVariable("id")Long id){
        return employeeService.UpdateById(id);
    }
}
