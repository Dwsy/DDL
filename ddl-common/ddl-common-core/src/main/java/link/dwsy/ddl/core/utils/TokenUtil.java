package link.dwsy.ddl.core.utils;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.Constants;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class TokenUtil {

    private static final String ISSUER = "签发者";

    public static String generateToken(LoginUserInfo loginUserInfo, Date expireDate) throws Exception {
        return Jwts.builder()
                // jwt payload --> KV
                .claim(Constants.JWT_USER_INFO_KEY, new ObjectMapper().writeValueAsString(loginUserInfo))
                // jwt id
                .setId(UUID.randomUUID().toString())
                // jwt 过期时间
                .setExpiration(expireDate)
                // jwt 签名 --> 加密
                .signWith(SignatureAlgorithm.RS256, RSAUtil.getPrivateKey())
                .compact();
    }

    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    /**
     * <h2>从 JWT Token 中解析 LoginUserInfo 对象</h2>
     */
    public static LoginUserInfo parseUserInfoFromToken(String token) {

        if (null == token) {
            return null;
        }

        Jws<Claims> claimsJws = parseToken(token);
        Claims body = claimsJws.getBody();

//        // 如果 Token 已经过期了, 返回 null
//        if (body.getExpiration().before(Calendar.getInstance().getTime())) {
//            return null;
//        }

        // 返回 Token 中保存的用户信息
        return JSON.parseObject(
                body.get(Constants.JWT_USER_INFO_KEY).toString(),
                LoginUserInfo.class
        );
    }

    /**
     * <h2>通过公钥去解析 JWT Token</h2>
     */
    private static Jws<Claims> parseToken(String token) {
        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parser().setSigningKey(RSAUtil.getPublicKey()).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.error("token过期");
            throw new CodeException(CustomerErrorCode.UserTokenExpired);
        } catch (Exception e) {
            log.error("token解析失败");
            throw new CodeException(CustomerErrorCode.UserTokenExpired);
        }
        return claimsJws;
    }

    public static String getTokenDigest(String token) {
        return SecureUtil.md5(token + token.substring(10, 20));
    }



}
