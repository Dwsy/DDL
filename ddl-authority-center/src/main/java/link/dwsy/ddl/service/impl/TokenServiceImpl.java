package link.dwsy.ddl.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import link.dwsy.ddl.constant.AuthorityConstant;
import link.dwsy.ddl.constant.TokenConstant;
import link.dwsy.ddl.core.constant.Constants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.domain.UsernameAndPassword;
import link.dwsy.ddl.core.utils.RSAUtil;
import link.dwsy.ddl.dao.UserDao;
import link.dwsy.ddl.entity.tuser;

import link.dwsy.ddl.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static cn.hutool.crypto.OpensslKeyUtil.getPrivateKey;

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
    private UserDao userDao;

    @Override
    public String generateToken(String username, String password) throws Exception {

        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire)
            throws Exception {
        UsernameAndPassword usernameAndPassword = new UsernameAndPassword();
        usernameAndPassword.setUsername(username);
        usernameAndPassword.setPassword(password);


        QueryWrapper<tuser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", usernameAndPassword.getUsername());
        tuser user = userDao.selectOne(wrapper);
        if (user != null) {

        } else {
            log.error("can not find user: [{}], [{}]", username, password);
            return null;
//            throw new Exception("登录失败");
        }
//         首先需要验证用户是否能够通过授权校验, 即输入的用户名和密码能否匹配数据表记录

        // Token 中塞入对象, 即 JWT 中存储的信息, 后端拿到这些信息就可以知道是哪个用户在操作
        LoginUserInfo loginUserInfo = new LoginUserInfo(
                user.getId(), user.getUsername()
        );

        if (expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        }

        // 计算超时时间
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());

        return Jwts.builder()
                // jwt payload --> KV
                .claim(Constants.JWT_USER_INFO_KEY, JSONUtil.toJsonStr(loginUserInfo))
                // jwt id
                .setId(UUID.randomUUID().toString())
                // jwt 过期时间
                .setExpiration(expireDate)
                // jwt 签名 --> 加密
                .signWith(SignatureAlgorithm.RS256, RSAUtil.getPrivateKey())
                .compact();
    }


    @Override
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword)
            throws Exception {

        // 先去校验用户名是否存在, 如果存在, 不能重复注册
        QueryWrapper<tuser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", usernameAndPassword.getUsername());
        tuser user = userDao.selectOne(wrapper);
        if (user != null) {
            log.error("username is registered: [{}]", user.getUsername());
            return null;
        }
        user = new tuser();
        user.setUsername(usernameAndPassword.getUsername());
        user.setPassword(usernameAndPassword.getPassword());//todo 前端加密 后期加盐
        userDao.insert(user);
        // 注册一个新用户, 写一条记录到数据表中
        log.info("register user success: [{}], [{}]", user.getUsername(),
                user.getId());
        // 生成 token 并返回
        return generateToken(user.getUsername(), user.getPassword());
    }


}
