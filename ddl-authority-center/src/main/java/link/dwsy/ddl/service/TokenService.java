package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.RB.UserRB;
import link.dwsy.ddl.XO.RB.UserRegisterRB;

/**
 * @Author Dwsy
 * @Date 2022/8/16
 */
public interface TokenService {
    /**
     * <h2>生成 JWT Token, 使用默认的超时时间</h2>
     * */
    String generateToken(UserRB userRB) throws Exception;

    /**
     * <h2>生成指定超时时间的 Token, 单位是天</h2>
     * */
    String generateToken(UserRB userRB, int expire) throws Exception;

    /**
     * <h2>注册用户并生成 Token 返回</h2>
     * */

    String registerUserAndGenerateToken(UserRegisterRB userRegisterRB)
            throws Exception;
}
