package noob.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import noob.reggie.domain.dto.DishDto;
import noob.reggie.domain.entity.Category;
import noob.reggie.domain.entity.Dish;
import noob.reggie.domain.entity.DishFlavor;
import noob.reggie.service.CategoryService;
import noob.reggie.service.DishFlavorService;
import noob.reggie.service.DishService;
import noob.reggie.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2022-04-16 19:56:21
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        save(dishDto);
        // 口味
        dishDto.getFlavors().stream()
                .forEach(dishFlavor -> dishFlavor.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        updateById(dishDto);
        final LambdaUpdateWrapper<DishFlavor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(updateWrapper);
        dishDto.getFlavors().stream()
                .forEach(dishFlavor -> dishFlavor.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public Page<DishDto> page(Integer page, Integer pageSize, String name) {
        final Page<Dish> dishPage = new Page<>(page, pageSize);
        final LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        page(dishPage, queryWrapper);

        final Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        final List<Dish> records = dishPage.getRecords();
        // 根据id获取分类对象
        final Set<Long> categoryIds = records.stream().map(dish -> dish.getCategoryId()).collect(Collectors.toSet());
        final List<Category> categoryList = categoryService.listByIds(categoryIds);
        // 遍历集合处理dish 转化成 dishDto
        final List<DishDto> dishDtos = records.stream().map(dish -> {
            final DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            categoryList.stream().anyMatch(category -> {
                if (category.getId().equals(dish.getCategoryId())) {
                    dishDto.setCategoryName(category.getName());
                    return true;
                }
                return false;
            });
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtos);
        return dishDtoPage;
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        final Dish dish = getById(id);
        final DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        final LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        final List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }
}




