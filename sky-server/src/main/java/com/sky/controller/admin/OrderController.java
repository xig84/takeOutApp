package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@Slf4j
@Api(tags = "admin端order接口")
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索,{}",ordersPageQueryDTO);
        PageResult pageResult =  orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("各个状态的订单的数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statisticsOrder(){
        log.info("各状态订单数量统计");

        OrderStatisticsVO orderStatisticsVO = orderService.statisticsOrder();

        return Result.success(orderStatisticsVO);
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> details (@PathVariable Long id){

        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }


    //- 商家接单其实就是将订单的状态修改为“已接单”
    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("商家接单,{}",ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /*
        商家拒单其实就是将订单状态修改为“已取消”
    - 只有订单处于“待接单”状态时可以执行拒单操作
    - 商家拒单时需要指定拒单原因
    - 商家拒单时，如果用户已经完成了支付，需要为用户退款
     */
    @ApiOperation("商家拒单")
    @PutMapping("/rejection")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("商家拒单");
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancelOrder (@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("商家取消订单");
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单");
        orderService.delivery(id);
        return Result.success();
    }

    @ApiOperation("完成订单")
    @PutMapping("complete/{id}")
    public Result complete( @PathVariable Long id){
        log.info("完成订单,{}",id);

        orderService.complete(id);
        return Result.success();
    }
}

