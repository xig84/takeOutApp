package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;

import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;



@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setMealMapper;
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        //判断当前加入购物车的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果存在，只需要将数量加1
        if (list!=null && list.size() > 0){
            ShoppingCart shoppingCart1 = list.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart1);
        }else {
            //如果不存在，需要加入一条新商品数据
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null){
                //本次添加到购物车的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                //本次添加到购物车的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setMealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            log.info("{}",shoppingCart);
            shoppingCartMapper.insert(shoppingCart);

        }

    }

    /**
     * 查看购物车
      * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart =  ShoppingCart.builder()
                        .userId(userId)
                                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void subOne(ShoppingCartDTO shoppingCartDTO) {
        //判断要删除的菜品/setmeal在购物车中的数量
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        shoppingCart = shoppingCartList.get(0);
        //如果数量 == 1 删除掉该商品的记录
        if (shoppingCart.getNumber() == 1) {
            //判断该商品是setmeal or dish
            //dish 可能会有flavor
            if (shoppingCart.getDishId() != null) {
                ShoppingCart shoppingCart1 = ShoppingCart.builder()
                        .dishId(shoppingCart.getDishId())
                        .dishFlavor(shoppingCart.getDishFlavor())
                        .build();
                shoppingCartMapper.deleteByDishIdWithFlavorOrSetmealId(shoppingCart1);
            } else {
                //该商品是setmeal
                ShoppingCart shoppingCart1 = ShoppingCart.builder()
                        .setmealId(shoppingCart.getSetmealId())
                        .build();
                shoppingCartMapper.deleteByDishIdWithFlavorOrSetmealId(shoppingCart1);
            }
        } else {
            //如果数量> 1, number -1,执行update操作
            if (shoppingCart.getDishId() != null) {
                ShoppingCart shoppingCart1 = ShoppingCart.builder()
                        .dishId(shoppingCart.getDishId())
                        .dishFlavor(shoppingCart.getDishFlavor())
                        .number(shoppingCart.getNumber() - 1)
                        .build();
                shoppingCartMapper.updateNumberByDishIdWithFlavorOrSetmealId(shoppingCart1);
            } else {
                //该商品是setmeal
                ShoppingCart shoppingCart1 = ShoppingCart.builder()
                        .setmealId(shoppingCart.getSetmealId())
                        .number(shoppingCart.getNumber() - 1)
                        .build();
                shoppingCartMapper.updateNumberByDishIdWithFlavorOrSetmealId(shoppingCart1);

            }
        }
    }


}
