package com.inquistivecat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inquistivecat.commom.Result;
import com.inquistivecat.dto.SetmealDto;
import com.inquistivecat.entity.Category;
import com.inquistivecat.entity.Setmeal;
import com.inquistivecat.service.CategoryService;
import com.inquistivecat.service.SetmealDishService;
import com.inquistivecat.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 * @author hp
 */
@RequestMapping("/setmeal")
@RestController
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return Result.success("添加成功");
    }

    @GetMapping("/page")
    public Result<Page> listPage(int page,int pageSize,String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<Setmeal>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            Category category = categoryService.getById(item.getCategoryId());
            if (category!=null) {
                String s = category.getName();
                setmealDto.setCategoryName(s);
            }
            BeanUtils.copyProperties(item,setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return Result.success(setmealDtoPage);
    }


    @DeleteMapping
    public Result<String> remove(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return Result.success("删除成功");
    }

    /**
     * 根据条件来查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return Result.success(list);


    }

}
