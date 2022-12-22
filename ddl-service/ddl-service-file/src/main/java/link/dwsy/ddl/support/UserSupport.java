package link.dwsy.ddl.support;

import cn.hutool.core.util.StrUtil;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */
@Component
public class UserSupport {
    public LoginUserInfo getCurrentUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return TokenUtil.parseUserInfoFromToken(token);
    }
}
