package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setmealMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();

        //对象属性拷贝
        BeanUtils.copyProperties(categoryDTO,category);

        category.setStatus(StatusConstant.DISABLE);

        //设置当前记录创建时间和修改时间
        //category.setCreateTime(LocalDateTime.now());
       // category.setUpdateTime(LocalDateTime.now());

        //设置当前记录的创建人id和修改人id
        //category.setCreateUser(BaseContext.getCurrentId());
       // category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);
    }

    /*
    类别分页查询
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();

        categoryMapper.update(category);
    }

    @Override
    public Category getById(Long id) {


        Category category = categoryMapper.getById(id);

        return category;
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        //对象属性拷贝
        BeanUtils.copyProperties(categoryDTO,category);
      //  category.setUpdateTime(LocalDateTime.now());
        //category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    @Override
    public void deleteById(Long id) {

        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.delete(id);
    }

    @Override
    public List<Category> list(Integer type) {

        List<Category>  categoryList= categoryMapper.selectByType(type);
        return categoryList;
    }
}
