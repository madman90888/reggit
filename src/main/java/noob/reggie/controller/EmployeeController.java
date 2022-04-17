package noob.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import noob.reggie.domain.entity.Employee;
import noob.reggie.domain.pojo.R;
import noob.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {
        // 1.对密码进行 MD5 加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2.查询用户是否存在
        final Employee emp = employeeService.getEmployeeByUsername(employee.getUsername());
        if (ObjectUtils.isEmpty(emp)) {
            return R.error("用户名不存在");
        }

        // 3.密码是否正确
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误！");
        }

        // 4.状态是否正常
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

        // 5.登录成功，存储
        session.setAttribute("employee", emp.getId());
        return R.success(employee);
    }

    @PostMapping("logout")
    public R logout(HttpSession session) {
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R save(@RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        final Page<Employee> pageInfo = new Page<>(page, pageSize);
        final LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    @GetMapping("{id}")
    public R<Employee> getEmployee(@PathVariable Long id) {
        final Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
