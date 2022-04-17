package noob.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import noob.reggie.domain.dto.SetmealDto;
import noob.reggie.domain.entity.Setmeal;
import noob.reggie.domain.entity.SetmealDish;
import noob.reggie.domain.pojo.R;
import noob.reggie.service.SetmealDishService;
import noob.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    @GetMapping("page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        final Page<SetmealDto> dtoPage = setmealService.pageWithCategory(page, pageSize, name);
        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        final LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.in(Setmeal::getId, ids);
        final int count = setmealService.count(queryWrapper);
        if (count > 0) {
            return R.error("套餐正在售卖中，无法直接删除...");
        }

        final LambdaUpdateWrapper<SetmealDish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(updateWrapper);

        setmealService.removeByIds(ids);

        return R.success("删除成功");
    }

    @PostMapping("status/{status}")
    public R<String> update(@PathVariable Integer status,@RequestParam List<Long> ids) {
        final LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, status);
        updateWrapper.in(Setmeal::getId, ids);
        setmealService.update(updateWrapper);
        return R.success("状态修改成功");
    }

    @GetMapping("list")
    public R list(Setmeal setmeal) {
        final LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        final List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
