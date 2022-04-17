package noob.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import noob.reggie.domain.entity.AddressBook;
import noob.reggie.service.AddressBookService;
import noob.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-04-17 18:04:40
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




