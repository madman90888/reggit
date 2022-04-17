package noob.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import noob.reggie.domain.entity.DishFlavor;
import noob.reggie.service.DishFlavorService;
import noob.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-04-16 21:49:02
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




