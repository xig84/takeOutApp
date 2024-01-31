package com.sky.service;


import com.sky.dto.DishDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface DishService {

    //新增菜品和对应的口味数据
    public void saveWithFlavor(DishDTO dishDTO);
}
