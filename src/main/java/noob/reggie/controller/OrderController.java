package noob.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import noob.reggie.domain.entity.Orders;
import noob.reggie.domain.pojo.R;
import noob.reggie.service.OrdersService;
import noob.reggie.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("userPage")
    public R<Page<Orders>> userPage(Integer page, Integer pageSize) {
        final Page<Orders> ordersPage = new Page<>(page, pageSize);
        final LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }

    @PostMapping("loginout")
    public R<String> loginout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出登录");
    }
}
