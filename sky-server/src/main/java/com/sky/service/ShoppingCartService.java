package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCart();

    void cleanShoppingCart();

    /**
     * 删除shoppingCart中的一个商品
     * @param shoppingCartDTO
     */
    void subOne(ShoppingCartDTO shoppingCartDTO);
}
