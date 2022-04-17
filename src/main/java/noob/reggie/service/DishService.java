package noob.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import noob.reggie.domain.dto.DishDto;
import noob.reggie.domain.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-04-16 19:56:21
*/
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    Page<DishDto> page(Integer page, Integer pageSize, String name);

    DishDto getByIdWithFlavor(Long id);
}
