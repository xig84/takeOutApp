package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;


    @ApiOperation("新增套餐")
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
            log.info("新增套餐,{}",setmealDTO);
            setMealService.save(setmealDTO);
            return Result.success();
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询,{}",setmealPageQueryDTO);

        PageResult pageResult =  setMealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    @ApiOperation("批量套餐删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量套餐删除,ids:{}",ids);
        setMealService.deleteBatch(ids);
        return Result.success();
    }

    @ApiOperation("套餐的起售与停售")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("setmeal的起售与停售");
        setMealService.startOrStop(status,id);
        return  Result.success();
    }

    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id ){
        log.info("根据id:{}查询套餐",id);
        SetmealVO setmealVO =  setMealService.getById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("修改套餐 and related setmeal dishes")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){

        log.info("修改套餐,{}",setmealDTO);
        setMealService.updateWithSetMealDishes(setmealDTO);
        return Result.success();
    }
}
