package noob.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import noob.reggie.domain.entity.ShoppingCart;
import noob.reggie.service.ShoppingCartService;
import noob.reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-04-17 20:20:26
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




