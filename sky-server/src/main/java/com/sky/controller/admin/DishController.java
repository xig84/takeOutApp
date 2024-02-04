package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */

@RestController
@Slf4j
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result  save(@RequestBody DishDTO dishDTO){

        log.info("新增菜品");
        dishService.saveWithFlavor(dishDTO);

        //清理缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();

    }

    /**
     * 菜品分页查询
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
            log.info("菜品分页查询,{}",dishPageQueryDTO);

            PageResult pageResult =  dishService.pageQuery(dishPageQueryDTO);

            return Result.success(pageResult);
    }

    @ApiOperation("批量菜品删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品,{}",ids);

        dishService.deleteBatch(ids);

        cleanCache("dish_*");

        return Result.success();
    }
    @ApiOperation("根据id查询菜品及flavors")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id ){

        log.info("根据id,查询菜品,{}",id);

        DishVO dishVO =  dishService.getByIdWithFlavor(id);

        return Result.success(dishVO);

    }

    @PutMapping()
    @ApiOperation("修改dish and related flavors")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改dish and flavors,{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        cleanCache("dish_*");



        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品的停售与起售")
    // solved bug:菜品停售以后，关联的套餐也应该停售;
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("菜品的停售与起售,{},{}",id,status);
        dishService.startOrStop(status,id);

        cleanCache("dish_*");


        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据categoryId查询dish")
    public Result<List<Dish>> list (Long categoryId){
        log.info("根据categoryId:{}查询dish",categoryId);

        List<Dish> dishList = dishService.list(categoryId);

        return Result.success(dishList);
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        //清理缓存数据，删除所有pattern开头的缓存数据
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }
}
