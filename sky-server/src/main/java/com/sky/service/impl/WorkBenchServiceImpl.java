package com.sky.service.impl;


import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkBenchService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkBenchServiceImpl implements WorkBenchService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询今日运营数据，涉及user表 order表
     * 需返回 当日 newUsers orderCompletionRate turnover uniPrice vaildOrderCount
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {

        LocalDate todayDate = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(todayDate,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(todayDate,LocalTime.MAX);
        Map map = new HashMap<>();
        map.put("end",endTime);
        map.put("begin",beginTime);
        // get newusers
        Integer newUsers = userMapper.countByMap(map);
        // get turnover
        //查询每天订单总数
        Integer orderCount = orderMapper.countByMap(map);
        //查询每天有效订单数
        map.put("status", Orders.COMPLETED);
        Integer vaildOrderCount = orderMapper.countByMap(map);
        //订单完成率
        Double orderCompletionRate = 0.0;
        if (orderCount!= 0){
            orderCompletionRate =vaildOrderCount.doubleValue() / orderCount;
        }
        //查询营业额
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0:turnover;
        Double uniPrice = 0.0;
        //平均客单价 = 总销售额/有效订单数
        if(vaildOrderCount!=0){
            uniPrice = turnover / vaildOrderCount;
        }
        return BusinessDataVO.builder()
                .validOrderCount(vaildOrderCount)
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(uniPrice)
                .turnover(turnover)
                .build();
    }

    /**
     * allOrders cancelledOrders completedOrders deliveredOrders waitingOrders
     * @return
     */
    @Override
    public OrderOverViewVO getorderOverView() {
        LocalDate todaytDate = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(todaytDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(todaytDate,LocalTime.MAX);
        Map map = new HashMap<>();
        map.put("end",endTime);
        map.put("begin",beginTime);
        Integer allOrders = orderMapper.countByMap(map);
        map.put("status",Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);
        map.put("status",Orders.DELIVERY_IN_PROGRESS);
        Integer deliveredOrders = orderMapper.countByMap(map);
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .completedOrders(completedOrders)
                .build();
    }

    /**
     * discontinued sold
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverview() {

        Integer sold = setMealMapper.countByStatus(Setmeal.ON_SALE);
        Integer discontinued = setMealMapper.countByStatus(Setmeal.DIS_CONTINUTED);
        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    @Override
    public DishOverViewVO getDishOverview() {
        Integer sold = dishMapper.countByStatus(Dish.ON_SALE);
        Integer discontinued = dishMapper.countByStatus(Dish.DIS_CONTINUTED);
        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }
}
