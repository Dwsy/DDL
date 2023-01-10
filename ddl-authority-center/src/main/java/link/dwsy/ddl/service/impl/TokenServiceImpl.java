package link.dwsy.ddl.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import link.dwsy.ddl.XO.RB.UserRB;
import link.dwsy.ddl.XO.RB.UserRegisterRB;
import link.dwsy.ddl.constant.AuthorityConstant;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.Constants;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenParseUtil;
import link.dwsy.ddl.core.utils.TokenUtil;
import link.dwsy.ddl.entity.User;
import link.dwsy.ddl.entity.UserInfo;
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
            if (user == null) {
                log.error("can not find user: [{}],", userRB.getUsername());
//                return "username or password error";
            throw new CodeException("username or password error");
            }
            String md5Pwd = SecureUtil.md5(userRB.getPassword() + user.getSalt());

            if (!user.getPassword().equals(md5Pwd)) {
//                log.error("can not find user: [{}],", userRB.getUsername());
                throw new CodeException("username or password error");
            }
            user = userRepository.findUserByUsernameAndPasswordAndDeletedIsFalse(userRB.getUsername(), md5Pwd);
        } else {
            user = userRepository.findByDeletedFalseAndPhone(userRB.getPhone());
            if (user == null) {
                log.error("can not find user: [{}],", userRB.getUsername());
                throw new CodeException("username or password error");
            }
            String md5 = SecureUtil.md5(userRB.getPassword() + user.getSalt());
            if (!user.getPassword().equals(md5)) {
                //todo 密码多次错误锁定ip
//                log.error("can not find user: [{}],", userRB.getUsername());
                throw new CodeException("username or password error");
            }
        }
        // 首先需要验证用户是否能够通过授权校验, 即输入的用户名和密码能否匹配数据表记录
        if (user == null) {
            log.error("can not find user: [{}],", userRB.getUsername());
            throw new CodeException("username or password error");
//            throw new Exception("登录失败");
        }
        // Token 中塞入对象, 即 JWT 中存储的信息, 后端拿到这些信息就可以知道是哪个用户在操作
        return getToken(expire, user);
    }

    public String getToken(int expire, User user) throws Exception {
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
        String token = TokenUtil.generateToken(loginUserInfo, expireDate);
        redisTemplate.opsForValue().set(TokenConstants.REDIS_TOKEN_ACTIVE_TIME_KEY + TokenUtil.getTokenDigest(token),
                String.valueOf(System.currentTimeMillis()),
                AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
        redisTemplate.opsForValue()
                .set(Constants.RE_ACTIVE_TOKEN_KEY + TokenUtil.getTokenDigest(token), "1", 8, TimeUnit.HOURS);
        return token;
    }


    public void test(UserRB userRB) {
        System.out.println(userRB.getUsername());
        System.out.println(userRB.getPassword());
    }

    @Override
    public String registerUserAndGenerateToken(UserRegisterRB userRegisterRB) throws Exception {
        // {front} 明文密码-> rasDecode(md5(pwd)) ->
        // {server} pwd= rsaEncode(body.pwd) ->md5(pwd+salt~Random(8,str)) -> savePwd
        // 好像后端不需要解密, 直接保存就行了


        // 先去校验用户名是否存在, 如果存在, 不能重复注册
        User u = userRepository.findUserByUsernameAndDeletedIsFalse(userRegisterRB.getUsername());
        if (u != null) {
            log.error("username is registered: [{}]", userRegisterRB.getUsername());
//            return "username is registered";
            throw new CodeException("用户名已存在");
        }
        String registerRBPassword = userRegisterRB.getPassword();
        String salt = RandomUtil.randomString(8);
        String password = SecureUtil.md5(registerRBPassword + salt);


        User user = User.builder()
                .username(userRegisterRB.getUsername())
                .nickname(userRegisterRB.getUsername())
                .salt(salt)
                .password(password)
                .build();
        user.setUserInfo(UserInfo.builder()
                .avatar("https://tva4.sinaimg.cn/large/005NWBIgly1h6ffm4ez6bj30fo0fogra.jpg")
                .build());
        userRepository.save(user);
        // 注册一个新用户, 写一条记录到数据表中
        log.info("register user success: [{}], [{}],[{}]", user.getUsername(), user.getId(), user.getPassword());
        // 生成 token 并返回
        LoginUserInfo loginUserInfo = LoginUserInfo.builder()
                .username(user.getUsername())
                .id(user.getId())
                .nickname(user.getNickname()).build();
        var expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());
        return TokenUtil.generateToken(loginUserInfo, expireDate);
    }

    public void blackToken(String token) {
        redisTemplate.opsForValue().set(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + TokenUtil.getTokenDigest(token), "1",
                AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
    }

    public Boolean isBlackToken(String token) {
        return redisTemplate.hasKey(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + TokenUtil.getTokenDigest(token));
    }


    public String refreshToken(String token) throws Exception {
        LoginUserInfo loginUserInfo = null;
        if (token != null) {
            try {
                loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
            } catch (Exception ex) {
                log.error("parse user info from token error: [{}]", ex.getMessage(), ex);
                throw new CodeException(CustomerErrorCode.UserTokenExpired);
            }
        }
        assert loginUserInfo != null;
        User user = userRepository.findByDeletedFalseAndId(loginUserInfo.getId()).orElse(null);
        UserRB userRB = new UserRB();
        if (user != null) {
            userRB.setUsername(user.getUsername());
            userRB.setPassword(user.getPassword());
        } else {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        String newToken = generateToken(userRB);
        redisTemplate.opsForValue().set(TokenConstants.REDIS_TOKEN_BLACKLIST_KEY + token, "1",
                AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
        return newToken;
    }

    public boolean active(String token) {
        //延长token有效期
        if (isBlackToken(token)) {
            throw new CodeException(CustomerErrorCode.UserTokenExpired);
        }
        String key = TokenConstants.REDIS_TOKEN_ACTIVE_TIME_KEY + TokenUtil.getTokenDigest(token);
        long nowTime = System.currentTimeMillis();
        String lastActiveTimeStr = redisTemplate.opsForValue().get(key);
        Long expire = redisTemplate.getExpire(key);

        if (lastActiveTimeStr == null) {
            throw new CodeException(CustomerErrorCode.UserTokenExpired);
//            redisTemplate.opsForValue().set(key, String.valueOf(time), AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
        } else {
            expire = expire == null ? 0 : expire;
            long expireDate = nowTime + expire;
            long lastActiveTime = Long.parseLong(lastActiveTimeStr);
            if (lastActiveTime > expireDate) {
                throw new CodeException(CustomerErrorCode.UserTokenExpired);
            }
            if (lastActiveTime < nowTime - (1000L * 60 * 60 * 24 * 30)) {
                blackToken(token);
                throw new CodeException(CustomerErrorCode.UserTokenExpired);
            } else {
                redisTemplate.opsForValue().set(key, String.valueOf(nowTime), AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
                return true;
            }
        }

//        redisTemplate.opsForValue().set(key, String.valueOf(new Date().getTime()));
//        redisTemplate.opsForValue().set(key, String.valueOf(new Date().getTime()),
//                AuthorityConstant.DEFAULT_EXPIRE_DAY, TimeUnit.DAYS);
    }
}
