package noob.reggie.service;

import noob.reggie.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-04-16 18:48:24
*/
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
