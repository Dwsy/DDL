package link.dwsy.ddl.core.utils;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.*;
import link.dwsy.ddl.core.constant.Constants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

/**
 * <h1>JWT Token 解析工具类</h1>
 */
@Slf4j
public class TokenParseUtil {
    public static void main(String[] args) throws Exception {

        LoginUserInfo loginUserInfo = parseUserInfoFromToken("eyJhbGciOiJSUzI1NiJ9.eyJkZGwtdXNlciI6IntcImlkXCI6MzEsXC" +
                "J1c2VybmFtZVwiOlwiZHdzeTJcIn0iLCJqdGkiOiI1ZGVkOGNiOS1iOWMwLTQyMmUtYjY4OS1" +
                "lZjEwMTQ1YzIwYWEiLCJleHAiOjE2NjA2NjU2MDB9.gI82S0Ml8gn_3tNRoFVkKJiUtTXZmw2gi3" +
                "5niw_0GkRP5k7WZDORdls70L6pkUg8LBLrMs6KqjqvOdnBkb3yqxKVJMWLj_FKizK116Xd1Gjd2SrvK" +
                "jnelhxxUCz7MBA1s3B4GFRAtJIJpSwKZ3eQDFB5D-I_rIcSjeuRe0VIpqHtBCmcxo6x7nui1mM9ttS3hIyX" +
                "i750DMErlBgeqY--oIuz-9qd9gw0PNvdTCdT_2u-nH7TqBupMk14zMXD-X0lie8a1GK1J7DKBEC3wPnVTWoTZ2_f" +
                "nN0p8IBIT3Y0QvCLWB7RwpH5YCTqy-nq4ew-e6q_s6NqXW-owLhOfwrhNOkyiKhV3sqQg5_UvF31ZQn7sJfEQn0Ws3kQT" +
                "Q-BW2k2uzMXbCXtKzrLg614Qu33uNyOIflOJ0TtD2U4lX07995gsC6XwuA5ju04Yvq3WOpytDRiZrW6tBPg7-lttD17tjjtn" +
                "y54trSwFpD80-mZR2gpp1o2PLjIP9vnc_1f6ktUYrUusFvhG0V0szK8gyq2vYd4MDeoDW4z-Bh3hNoM5AMV8Zq9jf-GOPI_LctBu" +
                "lV7L7z0LK5CeKCR94IQdmeZ0DcSZ-3ABX28RPl1JIBajjOEgOnsC123X6RZ7GCviO6xA5QLAf9odr2MRYoX45vqOzKReG7lKbmwM8aBRY" +
                "WuC7EjvY");
        System.out.println(loginUserInfo);

    }

    /**
     * <h2>从 JWT Token 中解析 LoginUserInfo 对象</h2>
     */
    public static LoginUserInfo parseUserInfoFromToken(String token) throws Exception {

        if (null == token) {
            return null;
        }

        Jws<Claims> claimsJws = parseToken(token, RSAUtil.getPublicKey());
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
    public static Jws<Claims> parseToken(String token, PublicKey publicKey) throws Exception {
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.error("token过期");
            throw new Exception("token过期");
        } catch (SignatureException e) {
            log.error("非法token");
            throw new Exception("非法token");
        }
        return claimsJws;
    }


}
