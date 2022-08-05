package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.common.R;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import com.example.reggie.service.ICategoryService;
import com.example.reggie.service.IDIshFlavorService;
import com.example.reggie.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Resource
    private IDIshFlavorService dishFlavorService;
    @Resource
    private ICategoryService categoryService;
    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public R saveDish(DishDto dishDto) {
        save(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> flavorList=new ArrayList<>();
        for (DishFlavor flavor:flavors){
            flavor.setDishId(dishDto.getId());
            flavorList.add(flavor);
        }
        dishFlavorService.saveBatch(flavorList);
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @Override
    public R appearById(Long id) {
        Dish dish = query().eq("id", id).one();
        List<DishFlavor> dishFlavorList = dishFlavorService.query().eq("dish_id", id).list();
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavorList);
        return R.success(dishDto);
    }

    @Override
    public R deleteById(Long[] ids) {
        if (ids!=null){
            for (int i=0;i<ids.length;i++){
                removeById(ids[i]);
                dishFlavorService.removeById(ids[i]);
            }
            List<Dish> dishes = listByIds(Arrays.asList(ids));
            for (Dish dish:dishes){
                String key="dish_"+dish.getCategoryId()+"_1";
                redisTemplate.delete(key);
            }
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R stopBatchSell(Integer status, Long[] ids) {
        for (int i=0;i<ids.length;i++){
            update().setSql("status=" + status).eq("id", ids[i]).update();
        }
        List<Dish> dishes = listByIds(Arrays.asList(ids));
        for (Dish dish:dishes){
            String key="dish_"+dish.getCategoryId()+"_1";
            redisTemplate.delete(key);
        }
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        return R.success("批量修改成功！");
    }

    @Override
    public R getCategoryById(Dish dish) {
        List<DishDto> redisList =new ArrayList<>();
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        redisList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (redisList!=null){
            return R.success(redisList);
        }
        QueryWrapper<Dish> queryWrapper=new QueryWrapper<>();
       if (dish!=null){
           queryWrapper.eq("category_id",dish.getCategoryId());
       }
       queryWrapper.eq("status", 1).orderByAsc("sort").orderByDesc("update_time");
        List<Dish> list = list(queryWrapper);
        List<DishDto> dishDtoList=new ArrayList<>();
        for (Dish dishes:list){
            DishDto dto=new DishDto();
            BeanUtils.copyProperties(dishes,dto);
            Category category = categoryService.getById(dishes.getCategoryId());
            if (category!=null){
                dto.setCategoryName(category.getName());
            }
            List<DishFlavor> dishFlavors = dishFlavorService.query().eq("dish_id", dishes.getId()).list();
            dto.setFlavors(dishFlavors);
            dishDtoList.add(dto);
        }
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    @Override
    public R pageAll(Integer page, Integer pageSize, String name) {
        Page<Dish> dish=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>(page,pageSize);
        QueryWrapper<Dish> condition=new QueryWrapper<>();
        if (name!=null) {
            condition.like("name",name);
        }
        condition.orderByDesc("price");
        page(dish,condition);
        BeanUtils.copyProperties(dish,dishDtoPage,"records");
        List<Dish> records = dish.getRecords();
        List<DishDto> list=new ArrayList<>();
        for (Dish record:records){
            DishDto dto=new DishDto();
            BeanUtils.copyProperties(record,dto);
            String categoryName = categoryService.getById(record.getCategoryId()).getName();
            dto.setCategoryName(categoryName);
            list.add(dto);
        }
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @Override
    public R updateDto(DishDto dishDto) {
        QueryWrapper queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("dish_id",dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> flavorList=new ArrayList<>();
        for (DishFlavor flavor:flavors){
            flavor.setDishId(dishDto.getId());
            flavorList.add(flavor);
        }
        dishFlavorService.saveBatch(flavorList);
        updateById(dishDto);
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改完成");
    }

}

