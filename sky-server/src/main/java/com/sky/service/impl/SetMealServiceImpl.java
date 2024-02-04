package com.sky.service.impl;

import com.alibaba.druid.sql.parser.NotAllowCommentException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setmealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增套餐涉及两张表，setmeal 和 setmeal_dish
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.insert(setmeal);

        //获取insert语句生成的主键值
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();

        if (setmealDishList != null && setmealDishList.size() > 0){
            setmealDishList.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
           setMealDishMapper.insertBatch(setmealDishList);

        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<DishVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> records = page.getResult();



        return new PageResult(total,records);

    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //删除操作不仅要删除setmeal中的数据
        //还要删除掉setmeal_dish中关联的数据
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);

            if (setmeal.getStatus() == StatusConstant.ENABLE){

                throw  new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealMapper.deleteByIds(ids);
            setMealDishMapper.deleteBySetMealIds(ids);

        }
    }
    
    /**
     * setmeal start or stop sell
     * @param status 1-在售 0-停售
     * @param id setmeal 's id
     */
    @Override

    public void startOrStop(Integer status, Long id) {
        //如果套餐中包含未起售菜品，不能起售套餐
        //根据setmealId 在setmealdish表中找到dish_id,再联查dish表，循环全部dish的status
        //若有dish status为0，不能起售
        //停售无此限制

        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        //1 停售情况
        if (status == StatusConstant.DISABLE) {

            setmealMapper.update(setmeal);

        } else {
            List<Dish> dishList = setMealDishMapper.getDishIdsBySetMealId(id);

            if (dishList != null && dishList.size() > 0) {
                dishList.forEach(dish -> {
                    if (dish.getStatus() == 0) {
                        throw new  DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });

            }
            setmealMapper.update(setmeal);
        }
    }

    @Override
    public SetmealVO getById(Long id) {

        //查询setmeal 也要返回setmeal_dish的相关数据

        Setmeal setmeal = setmealMapper.getById(id);

        List<SetmealDish> setmealDishList = setMealDishMapper.getSetmealDishsBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();

        BeanUtils.copyProperties(setmeal,setmealVO);

        setmealVO.setSetmealDishes(setmealDishList);

        return setmealVO;



    }

    @Override
    public void updateWithSetMealDishes(SetmealDTO setmealDTO) {
        //修改setmeal及 related setmeal_dishes

        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        //setmale_dish table的修改：先把原先的表全部删除，再按照user输入重新插入

        setMealDishMapper.deleteBySetMealId(setmeal.getId());

        List<SetmealDish>  setmealDishList = setmealDTO.getSetmealDishes();

        if (setmealDishList != null && setmealDishList.size() > 0){
            setmealDishList.forEach(setmealDish-> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });

            setMealDishMapper.insertBatch(setmealDishList);
        }
    }

    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }


    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
