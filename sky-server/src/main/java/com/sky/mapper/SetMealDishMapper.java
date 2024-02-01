package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {


    // select setMeal_id from setmeal_dish where dish_id in (1,2,3,4,..)
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);


    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishList);

    void deleteBySetMealIds(List<Long> setMealIds);

    List<Dish> getDishIdsBySetMealId(Long id);


    List<SetmealDish> getSetmealDishsBySetmealId(Long setmealId);

    @Select("delete from setmeal_dish where setmeal_id = #{setMealId}")
    void deleteBySetMealId(Long setMealId);
}
