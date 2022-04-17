package noob.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import noob.reggie.domain.entity.*;
import noob.reggie.exception.CustomException;
import noob.reggie.service.*;
import noob.reggie.mapper.OrdersMapper;
import noob.reggie.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2022-04-17 21:26:40
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获取用户ID
        final Long currentId = BaseContext.getCurrentId();

        // 查询购物车
        final LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        final List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartQueryWrapper);
        if (ObjectUtils.isEmpty(shoppingCarts)) {
            throw new CustomException("购物车为空");
        }
        // 获取用户 默认地址
        final User user = userService.getById(currentId);
        final AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (ObjectUtils.isEmpty(addressBook)) {
            throw new CustomException("用户地址有误，下单失败");
        }

        // 插入订单
        final long orderId = IdWorker.getId();

        // 计算金额
        final AtomicInteger amount = new AtomicInteger(0);  // 多线程安全
        final List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
            final OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(currentId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        save(orders);


        // 插入多条订单记录
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车
        shoppingCartService.remove(shoppingCartQueryWrapper);
    }
}




