package noob.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import noob.reggie.domain.dto.DishDto;
import noob.reggie.domain.entity.Category;
import noob.reggie.domain.entity.Dish;
import noob.reggie.domain.entity.DishFlavor;
import noob.reggie.domain.pojo.R;
import noob.reggie.service.CategoryService;
import noob.reggie.service.DishFlavorService;
import noob.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        final Page<DishDto> dishDtoPage = dishService.page(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    @GetMapping("{id}")
    public R<DishDto> dish(@PathVariable Long id) {
        final DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    @PostMapping("status/{status}")
    public R<String> status(@PathVariable int status, String ids) {
        final LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status);
        final List<String> list = Arrays.asList(ids.split(","));
        updateWrapper.in(Dish::getId, list);
        dishService.update(updateWrapper);
        return R.success("状态修改成功");
    }

    @DeleteMapping
    public R<String> delete(String ids) {
        final List<String> list = Arrays.asList(ids.split(","));
        final LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, list);
        dishFlavorService.remove(queryWrapper);

        final LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, list);
        dishService.remove(updateWrapper);
        return R.success("删除成功");
    }

    @GetMapping("list")
    public R<List<DishDto>> list(Dish dish) {
        final LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        final List<Dish> list = dishService.list(queryWrapper);
        // 查询分类
        final Set<Long> categoryIds = list.stream().map(dish1 -> dish1.getCategoryId()).collect(Collectors.toSet());
        final List<Category> categoryList = categoryService.listByIds(categoryIds);
        // 查询口味
        final Set<Long> dishIds = list.stream().map(dish1 -> dish1.getId()).collect(Collectors.toSet());
        final LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId, dishIds);
        final List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
        final List<DishDto> dishDtos = list.stream().map(dish1 -> {
            final DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            categoryList.stream().anyMatch(category -> {
                if (category.getId().equals(dish1.getCategoryId())) {
                    dishDto.setCategoryName(category.getName());
                    return true;
                }
                return false;
            });
            final List<DishFlavor> flavors = dishFlavors.stream().filter(dishFlavor -> dishFlavor.getDishId().equals(dish1.getId()))
                    .collect(Collectors.toList());
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }

}
