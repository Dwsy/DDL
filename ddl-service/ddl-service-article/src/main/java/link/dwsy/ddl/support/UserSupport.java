package link.dwsy.ddl.support;

import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Component
public class UserSupport {

    public LoginUserInfo getCurrentUser() throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        if (StrUtil.isBlank(token)) {
            throw new CodeException(2, "token为空");
        }
//        Long userId = TokenUtil.parseUserInfoFromToken(token).getId();
//        if(userId < 0) {
//            throw new Exception("非法用户");
//        }
//        this.verifyRefreshToken(userId);

        return TokenUtil.parseUserInfoFromToken(token);

    }

    private void verifyRefreshToken(Long userId) {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//        String refreshToken = requestAttributes.getRequest().getHeader("refreshToken");
//        String dbRefreshToken = userService.getRefreshTokenByUserId(userId);
//        if(!dbRefreshToken.equals(refreshToken)){
//            throw new ConditionException("非法用户！");
//        }
    }
}
