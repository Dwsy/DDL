package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import link.dwsy.ddl.XO.RB.UserRB;
import link.dwsy.ddl.XO.RB.UserRegisterRB;
import link.dwsy.ddl.constant.AuthorityConstant;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenParseUtil;
import link.dwsy.ddl.core.utils.TokenUtil;
import link.dwsy.ddl.entity.User;
import link.dwsy.ddl.repository.UserRepository;
import link.dwsy.ddl.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public String generateToken(UserRB userRB) throws Exception {

        return generateToken(userRB, 0);
    }

//   前端 (pwd + PublicKey -> md5) 后端( md5 (md5+salt) -> savePwd)

    @Override
    public String generateToken(UserRB userRB, int expire)
            throws Exception {
        User user = null;
        if (!StrUtil.hasBlank(userRB.getUsername())) {
            user = userRepository.findByDeletedFalseAndUsername(userRB.getUsername());
            String md5 = SecureUtil.md5(userRB.getPassword() + user.getSalt());
            if (!user.getPassword().equals(md5)) {
                return "0";
            }
            user = userRepository.findUserByUsernameAndPasswordAndDeletedIsFalse(userRB.getUsername(), userRB.getPassword());
        } else {
            user = userRepository.findByDeletedFalseAndPhone(userRB.getPhone());

            String md5 = SecureUtil.md5(userRB.getPassword() + user.getSalt());
            if (!user.getPassword().equals(md5)) {
                return "0";
            }
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
    public String registerUserAndGenerateToken(UserRegisterRB userRegisterRB) throws Exception {


        // 先去校验用户名是否存在, 如果存在, 不能重复注册
        User u = userRepository.findUserByUsernameAndDeletedIsFalse(userRegisterRB.getUsername());
        if (u != null) {
            log.error("username is registered: [{}]", userRegisterRB.getUsername());
            return null;
        }

        String salt = RandomUtil.randomString(8);
        String password = SecureUtil.md5(userRegisterRB.getPassword() + salt);


        User user = User.builder()
                .username(userRegisterRB.getUsername())
                .nickname(userRegisterRB.getUsername())
                .salt(salt)
                .password(userRegisterRB.getPassword())
                .build();//todo 前端加密 后期加盐
        userRepository.save(user);
        // 注册一个新用户, 写一条记录到数据表中
        log.info("register user success: [{}], [{}]", user.getUsername(),
                user.getId());
        // 生成 token 并返回
        UserRB userRB = new UserRB();
        userRB.setUsername(userRegisterRB.getUsername());
        userRB.setPassword(password);

        return generateToken(userRB);
    }

    public void blackToken(String token) {
        redisTemplate.opsForValue().set(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + token, "1",
                AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
    }

    public String refreshToken(String token) throws Exception {
        LoginUserInfo loginUserInfo = null;
        if (token != null) {
            try {
                loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
            } catch (Exception ex) {
                log.error("parse user info from token error: [{}]", ex.getMessage(), ex);
                throw  new CodeException(CustomerErrorCode.TokenParseError);
            }
        }
        assert loginUserInfo != null;
        User user = userRepository.findByDeletedFalseAndId(loginUserInfo.getId()).orElse(null);
        UserRB userRB = new UserRB();
        if (user != null) {
            userRB.setUsername(user.getUsername());
            userRB.setPassword(user.getPassword());
        }else {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        String newToken = generateToken(userRB);
        redisTemplate.opsForValue().set(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + token, "1",
                AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
        return newToken;
    }
}
