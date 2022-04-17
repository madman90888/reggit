package noob.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import noob.reggie.domain.entity.Category;
import noob.reggie.domain.pojo.R;
import noob.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    @DeleteMapping
    public R<String> delete(long ids) {
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    @GetMapping("page")
    public R<Page> page(int page, int pageSize) {
        final Page<Category> pageInfo = new Page<>(page, pageSize);
        final LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @GetMapping("list")
    public R<List<Category>> list(Category category) {
        final LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        final List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
