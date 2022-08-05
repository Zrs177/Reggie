package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.common.R;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import com.example.reggie.service.ICategoryService;
import com.example.reggie.service.IDishService;
import com.example.reggie.service.ISetmealService;
import com.example.reggie.service.ISetmealdishService;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Transactional
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {
    @Resource
    private ISetmealdishService setmealdishService;
    @Resource
    private ICategoryService categoryService;
    @Resource
    private IDishService dishService;
    @Override
    @Transactional
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R saveSetmeal(SetmealDto setmealDto) {
        save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> setmealDishList=new ArrayList<>();
        for (SetmealDish setmealDish:setmealDishes){
                setmealDish.setSetmealId(setmealDto.getId());
                setmealDishList.add(setmealDish);
        }
        setmealdishService.saveBatch(setmealDishList);

        return R.success("添加成功");
    }

    @Override
    public R PageAll(Integer page, Integer pageSize, String name) {
        Page<Setmeal> setmealPage=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>(page,pageSize);
        QueryWrapper<Setmeal> condition=new QueryWrapper<>();
        if (name!=null) {
            condition.like("name",name);
        }
        condition.orderByDesc("price");
        page(setmealPage,condition);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list=new ArrayList<>();
        for (Setmeal record:records){
           SetmealDto dto=new SetmealDto();
            BeanUtils.copyProperties(record,dto);
            String categoryName = categoryService.getById(record.getCategoryId()).getName();
            dto.setCategoryName(categoryName);
            list.add(dto);
        }
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @Override
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R deleteById(Long[] ids) {
        for (int i=0;i<ids.length;i++){
                removeById(ids[i]);
                setmealdishService.removeById(ids[i]);
        }
        return R.success("删除成功");
    }

    @Override
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R stopBatchSell(Integer status, Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
                update().setSql("status=" + status).eq("id", ids[i]).update();
        }
        return R.success("批量修改成功！");
    }

    @Override
    public R appearById(Long id) {
        Setmeal setmeal = query().eq("id", id).one();
        List<SetmealDish> setmealDishes= setmealdishService.query().eq("id", id).list();
        SetmealDto dto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,dto);
        dto.setSetmealDishes(setmealDishes);
        return R.success(dto);
    }

    @Override
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R Update(SetmealDto setmealDto) {
        updateById(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> setmealDishList=new ArrayList<>();
        for (SetmealDish setmealDish:setmealDishes){
            setmealDish.setId(setmealDto.getId());
            setmealDishList.add(setmealDish);
        }
        setmealdishService.updateBatchById(setmealDishList);

        return R.success("修改菜品成功");
    }

    @Override
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R setmealList( Setmeal setmeal) {
        QueryWrapper<Setmeal> queryWrapper=new QueryWrapper<>();
        if (setmeal!=null){
            queryWrapper.eq("category_id",setmeal.getCategoryId());
            queryWrapper.eq("status",setmeal.getStatus());
            queryWrapper.orderByAsc("update_time");
        }
        List<Setmeal> list = list(queryWrapper);

        return R.success(list);
    }
}
