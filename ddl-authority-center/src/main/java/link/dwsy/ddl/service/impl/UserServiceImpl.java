package link.dwsy.ddl.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.domain.UsernameAndPassword;
import link.dwsy.ddl.entity.tuser;
import link.dwsy.ddl.service.UserService;
import link.dwsy.ddl.dao.UserDao;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
public class UserServiceImpl extends ServiceImpl<UserDao, tuser> implements UserService {

    @Override
    public tuser login(UsernameAndPassword usernameAndPassword) {
        QueryWrapper<tuser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", usernameAndPassword.getUsername());
        tuser user = this.getOne(wrapper);
        if (user!=null){
//todo 加密
            if (user.getPassword().equals(usernameAndPassword.getPassword())) {
                return user;
            }
//            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//            boolean matches = bCryptPasswordEncoder.matches(loginVo.getPassword(), entity.getPassword());
//            if (matches){
//                entity.setPassword("");
//                return entity;
//            }
        }
        return null;
    }
}
