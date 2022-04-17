package noob.reggie.service;

import noob.reggie.domain.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【employee(员工信息)】的数据库操作Service
* @createDate 2022-04-15 19:50:29
*/
public interface EmployeeService extends IService<Employee> {

    Employee getEmployeeByUsername(String username);
}
