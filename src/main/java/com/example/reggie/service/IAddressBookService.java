package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.pojo.AddressBook;

public interface IAddressBookService extends IService<AddressBook> {
    R saveAddress(AddressBook addressBook);

    R setDefault(AddressBook addressBook);

    R getDefault();

    R getALL();
}
