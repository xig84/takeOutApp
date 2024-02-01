package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);


    Page<DishVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select  * from  setmeal where id = #{id}")
    Setmeal getById(Long id);

    void deleteByIds(List<Long> ids);


    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    List<Setmeal> getByDishId(Long id);
}
