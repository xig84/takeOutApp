package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<DishFlavor> dishFlavorList);

    @Delete("delete  from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    void deleteByDishIds(List<Long> dishIds);


    List<DishFlavor> getByDishId(Long dishId);
}
