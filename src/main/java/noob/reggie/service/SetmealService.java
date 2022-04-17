package noob.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import noob.reggie.domain.dto.SetmealDto;
import noob.reggie.domain.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-04-16 19:56:21
*/
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    Page<SetmealDto> pageWithCategory(Integer page, Integer pageSize, String name);
}
