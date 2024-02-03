package com.sky.controller.admin;

/*
分类管理
 */

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "category相关interface")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*
    新增分类
     */
    @ApiOperation("新增分类")
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类");
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /*
    分类分页查询
     */
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询,{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("启用禁用category")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用或禁用categoty:{},{}",id,status);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    //根据id查询category
    @ApiOperation("根据id查询category")
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id ){
        log.info("根据id:{}查询category",id);
        Category category =  categoryService.getById(id);
        return Result.success(category);
    }

    @ApiOperation("编辑category")
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("编辑员工,{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @ApiOperation("根据id删除categoty")
    @DeleteMapping
    public  Result deleteById(Long id){
        log.info("根据id删除category,{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @ApiOperation("根据type查询category")
    @GetMapping("/list")
    public  Result<List<Category>> list (Integer type){
        List<Category> list  = categoryService.list(type);
        return Result.success(list);
    }

}
