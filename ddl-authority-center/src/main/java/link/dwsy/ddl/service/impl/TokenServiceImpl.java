package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import link.dwsy.ddl.constant.AuthorityConstant;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import link.dwsy.ddl.entity.User;

import link.dwsy.ddl.repository.UserRepository;
import link.dwsy.ddl.service.TokenService;
import link.dwsy.ddl.XO.RB.UserRB;
import link.dwsy.ddl.XO.RB.UserRegisterRB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TokenServiceImpl implements TokenService {
//implements TokenService

    @Resource
    private UserRepository userRepository;

    @Override
    public String generateToken(UserRB userRB) throws Exception {

        return generateToken(userRB, 0);
    }

    @Override
    public String generateToken(UserRB userRB, int expire)
            throws Exception {
        User user = null;
        if (!StrUtil.hasBlank(userRB.getUsername())) {
            user = userRepository.findUserByUsernameAndPasswordAndDeletedIsFalse(userRB.getUsername(), userRB.getPassword());
        } else {
            user = userRepository.findUserByPhoneAndPasswordAndDeletedIsFalse(userRB.getUsername(), userRB.getPassword());
        }
        if (user == null) {
            log.error("can not find user: [{}],", userRB.getUsername());
            return "0";
//            throw new Exception("登录失败");
        }
//         首先需要验证用户是否能够通过授权校验, 即输入的用户名和密码能否匹配数据表记录

        // Token 中塞入对象, 即 JWT 中存储的信息, 后端拿到这些信息就可以知道是哪个用户在操作
        LoginUserInfo loginUserInfo = LoginUserInfo.builder()
                .username(user.getUsername())
                .id(user.getId())
                .level(user.getLevel())
                .nickname(user.getNickname()).build();


        if (expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        }
        // 计算超时时间
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());

        return TokenUtil.generateToken(loginUserInfo, expireDate);
    }


    @Override
    public String registerUserAndGenerateToken(UserRegisterRB userRegisterRB)
            throws Exception {
        // 先去校验用户名是否存在, 如果存在, 不能重复注册
        User u = userRepository.findUserByUsernameAndDeletedIsFalse(userRegisterRB.getUsername());
        if (u != null) {
            log.error("username is registered: [{}]", userRegisterRB.getUsername());
            return null;
        }
        User user = User.builder()
                .username(userRegisterRB.getUsername())
                .password(userRegisterRB.getPassword())
                .userInfo(userRegisterRB.getUserInfo())
                .build();//todo 前端加密 后期加盐
        userRepository.save(user);
        // 注册一个新用户, 写一条记录到数据表中
        log.info("register user success: [{}], [{}]", user.getUsername(),
                user.getId());
        // 生成 token 并返回
        UserRB userRB = new UserRB();
        userRB.setUsername(userRegisterRB.getUsername());
        userRB.setPassword(userRegisterRB.getPassword());

        return generateToken(userRB);
    }
}
