package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品及对应口味数据，涉及多表操作,要开启事务
     * @param dishDTO
     */

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        //向dish表插入一条数据， 向dish_flavor插入n条数据

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();

        if (dishFlavorList != null && dishFlavorList.size() > 0){
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(dishFlavorList);

        }



    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> records = page.getResult();

        return new PageResult(total,records);

    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否可以删除-是否正在起售
        for(Long id:ids){
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE)
            {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //是否被套餐关联
        List<Long> setMealIds =  setMealDishMapper.getSetMealIdsByDishIds(ids);
        if (setMealIds != null && setMealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除dish数据 and 删除相关的dishflavor数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//           dishFlavorMapper.deleteByDishId(id);
//        }
        //优化上述删除代码，sql批量删除
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);

    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {

        //查询dish表 及 flavor表 然后把数据封装到dishVo.

        Dish dish = dishMapper.getById(id);

        List<DishFlavor> dishFlavors =  dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();

        BeanUtils.copyProperties(dish,dishVO);

        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {

        Dish dish  = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //flavor table 表的修改：先把原先的表全部删除，再按照user输入重新插入

        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        List<DishFlavor>  dishFlavorList = dishDTO.getFlavors();

        if (dishFlavorList != null && dishFlavorList.size() > 0){
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(dishFlavorList);
        }

    }

    @Override
    public void startOrStop(Integer status, Long id) {

        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();

        dishMapper.update(dish);


    }

    @Override
    public List<Dish> list(Long categoryId) {

       List<Dish> dishList = dishMapper.selectByType(categoryId);

       return dishList;
    }
}
