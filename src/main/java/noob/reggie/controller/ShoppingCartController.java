package noob.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import noob.reggie.domain.entity.ShoppingCart;
import noob.reggie.domain.pojo.R;
import noob.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public R<List> list(HttpSession session) {
        final LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        final List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("add")
    public R add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        shoppingCart.setUserId((Long) session.getAttribute("user"));
        // 判断购物车是否存在
        final Long dishId = shoppingCart.getDishId();
        final LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        queryWrapper.eq(dishId!=null, ShoppingCart::getDishId, dishId);
        queryWrapper.eq(shoppingCart.getSetmealId()!=null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart shopping = shoppingCartService.getOne(queryWrapper);
        // 存在
        if (shopping != null) {
            shopping.setNumber(shopping.getNumber() + 1);
            shoppingCartService.updateById(shopping);
        }else {
            shopping = shoppingCart;
            shopping.setNumber(1);
            shopping.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shopping);
        }

        return R.success(shopping);
    }

    @DeleteMapping("clean")
    public R<String> clean(HttpSession session) {
        final Long userId = (Long) session.getAttribute("user");
        final LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(updateWrapper);
        return R.success("清空购物车");
    }
}
