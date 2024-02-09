package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkBenchService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.inst2xsd.RussianDollStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "工作台相关接口")
@Slf4j
@RequestMapping("/admin/workspace")
public class WorkBenchController {

    @Autowired
    private WorkBenchService workBenchService;

    @ApiOperation("查询今日运营数据")
    @GetMapping("/businessData")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("查询今日运营数据");
        BusinessDataVO businessDataVO = workBenchService.getBusinessData();
        return Result.success(businessDataVO);
    }

    @ApiOperation("查询订单管理数据")
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overViewOrders(){
        log.info("查询今日订单相关数据");
        OrderOverViewVO orderOverViewVO = workBenchService.getorderOverView();
        return Result.success(orderOverViewVO);
    }

    @ApiOperation("查询setmeal总览")
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> setmealOverView(){
        log.info("setmeal总览");
        SetmealOverViewVO setmealOverViewVO = workBenchService.getSetmealOverview();
        return Result.success(setmealOverViewVO);
    }
    @ApiOperation("查询dish总览")
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> dishOverView(){
        log.info("dish总览");
        DishOverViewVO dishOverViewVO = workBenchService.getDishOverview();
        return Result.success(dishOverViewVO);
    }

}
