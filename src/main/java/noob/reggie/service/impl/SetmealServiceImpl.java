package noob.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import noob.reggie.domain.dto.SetmealDto;
import noob.reggie.domain.entity.Category;
import noob.reggie.domain.entity.Setmeal;
import noob.reggie.domain.entity.SetmealDish;
import noob.reggie.service.CategoryService;
import noob.reggie.service.SetmealDishService;
import noob.reggie.service.SetmealService;
import noob.reggie.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2022-04-16 19:56:21
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        save(setmealDto);
        final List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().forEach(setmealDish -> setmealDish.setSetmealId(setmealDto.getId()));

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public Page<SetmealDto> pageWithCategory(Integer page, Integer pageSize, String name) {
        final Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        final LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        page(setmealPage, queryWrapper);

        final Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, setmealDtoPage,"records");

        final List<Setmeal> records = setmealPage.getRecords();
        final Set<Long> ids = records.stream().map(setmeal -> setmeal.getCategoryId()).collect(Collectors.toSet());
        final List<Category> categoryList = categoryService.listByIds(ids);

        final List<SetmealDto> list = records.stream().map(setmeal -> {
            final SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            categoryList.stream().anyMatch(category -> {
                if (category.getId().equals(setmealDto.getCategoryId())) {
                    setmealDto.setCategoryName(category.getName());
                    return true;
                }
                return false;
            });
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return setmealDtoPage;
    }
}




