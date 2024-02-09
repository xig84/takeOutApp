package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkBenchService {
    BusinessDataVO getBusinessData();

    OrderOverViewVO getorderOverView();

    SetmealOverViewVO getSetmealOverview();

    DishOverViewVO getDishOverview();
}
