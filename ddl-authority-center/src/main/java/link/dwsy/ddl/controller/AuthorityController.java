package link.dwsy.ddl.controller;

import cn.hutool.json.JSONUtil;
import link.dwsy.ddl.XO.RB.UserRB;
import link.dwsy.ddl.XO.RB.UserRegisterRB;
import link.dwsy.ddl.annotation.IgnoreResponseAdvice;
import link.dwsy.ddl.core.domain.JwtToken;
import link.dwsy.ddl.core.utils.RSAUtil;
import link.dwsy.ddl.service.impl.TokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <h1>对外暴露的授权服务接口</h1>
 */
@Slf4j
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    @Resource
    private TokenServiceImpl tokenService;


    @GetMapping("/rsa-pks")
    public String getRsaPublicKey() {
        return RSAUtil.getPublicKeyStr();
    }

    /**
     * <h2>从授权中心获取 Token (其实就是登录功能), 且返回信息中没有统一响应的包装</h2>
     */
    @IgnoreResponseAdvice
    @PostMapping("/token")
    public JwtToken token(@RequestBody UserRB userRB)
            throws Exception {

        log.info("request to get token with param: [{}]",
                JSONUtil.toJsonStr(userRB));
        return new JwtToken(tokenService.generateToken(
                userRB
        ));
    }

    /**
     * <h2>注册用户并返回当前注册用户的 Token, 即通过授权中心创建用户</h2>
     */
    @IgnoreResponseAdvice
    @PostMapping("/register")
    public JwtToken register(@RequestBody UserRegisterRB userRegisterRB)
            throws Exception {

        log.info("register user with param: [{}]", JSONUtil.toJsonStr(
                userRegisterRB
        ));
        return new JwtToken(tokenService.registerUserAndGenerateToken(
                userRegisterRB
        ));
    }
}
