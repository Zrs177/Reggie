package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.pojo.AddressBook;
import com.example.reggie.service.IAddressBookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Resource
    private IAddressBookService addressBookService;
    @PostMapping
    public R saveAddress(@RequestBody AddressBook addressBook){
       return addressBookService.saveAddress(addressBook);
    }
    @PutMapping("/default")
    public R setDefault(@RequestBody AddressBook addressBook){
        return addressBookService.setDefault(addressBook);
    }
    @GetMapping("/{id}")
    public R getById(@PathVariable("id")Long id){
        return R.success(addressBookService.getById(id));
    }
    @GetMapping("/default")
    public R getDefault(){
        return addressBookService.getDefault();
    }
    @GetMapping("/list")
    public R getAll(){
        return addressBookService.getALL();
    }
    @PutMapping
    public R updateAddress(@RequestBody AddressBook addressBook){
        return R.success(addressBookService.updateById(addressBook));
    }
}
