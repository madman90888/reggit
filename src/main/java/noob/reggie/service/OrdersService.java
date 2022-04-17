package noob.reggie.service;

import noob.reggie.domain.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-04-17 21:26:40
*/
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
