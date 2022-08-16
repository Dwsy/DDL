package link.dwsy.ddl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.domain.UsernameAndPassword;
import link.dwsy.ddl.entity.tuser;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
public interface UserService extends IService<tuser> {
    tuser login(UsernameAndPassword usernameAndPassword);
}
