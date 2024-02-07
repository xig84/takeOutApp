package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController("userOrderController")
@Slf4j
@Api(tags = "用户端order相关接口")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单接口")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {

        log.info("用户下单，参数为:{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        log.info("调用模拟支付");
        orderService.paymentMock(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult> listHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("历史订单分页查询,{}",ordersPageQueryDTO);
        PageResult pageResult =orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id){

        log.info("根据id查询订单详情,{}",id);

        OrderVO orderVO = orderService.details(id);

        return Result.success(orderVO);

    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        log.info("取消订单:{}",id);
        orderService.cancelOrder(id);
        return Result.success();
    }

    //再来一单就是将原订单中的商品重新加入到购物车中
    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public Result repeatOrder(@PathVariable Long id){
        log.info("再来一单,{}",id);
        orderService.repeatOrder(id);
        return Result.success();
    }

    @ApiOperation("客户催单")
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id){

        orderService.reminder(id);
        return Result.success();
    }


}
