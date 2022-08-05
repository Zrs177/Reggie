package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.mapper.AddressBookMapper;
import com.example.reggie.pojo.AddressBook;
import com.example.reggie.service.IAddressBookService;
import com.example.reggie.util.BaseContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {
    @Override
    public R saveAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        save(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R setDefault(AddressBook addressBook) {
        update().setSql("is_default="+0).eq("user_id",BaseContext.getCurrentId()).update();
        addressBook.setIsDefault(1);
        updateById(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R getDefault() {
        AddressBook is_default = query().eq("is_default", 1).eq("user_id",BaseContext.getCurrentId()).one();
        if (is_default==null){
            return R.error("没有找到该对象");
        }
        return R.success(is_default);
    }

    @Override
    public R getALL() {
        List<AddressBook> user_id = query().eq("user_id", BaseContext.getCurrentId()).orderByDesc("update_time").list();
        return R.success(user_id);
    }
}
