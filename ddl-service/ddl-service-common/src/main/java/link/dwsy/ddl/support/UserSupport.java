package link.dwsy.ddl.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.cloud.nacos.discovery.enabled}")
    boolean isCloud=false;
//    cloud模式优先从header中获取loginUserInfo boot模式获取token
//    todo 缓存解析结果
    public LoginUserInfo getCurrentUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        if (isCloud) {
            String loginUserInfoString = request.getHeader("loginUserInfo");
            if (StrUtil.isBlank(loginUserInfoString)) {
                throw new CodeException(CustomerErrorCode.UserNotLogin);
            }
            return JSON.parseObject(loginUserInfoString, LoginUserInfo.class);
        } else {
            String token = request.getHeader(TokenConstants.AUTHENTICATION);
            if (StrUtil.isBlank(token)) {
                throw new CodeException(CustomerErrorCode.UserNotLogin);
            }
            String[] t = token.split(" ");
            return TokenUtil.parseUserInfoFromToken(t[1]);
        }
//        Long userId = TokenUtil.parseUserInfoFromToken(token).getId();
//        if(userId < 0) {
//            throw new Exception("非法用户");
//        }
//        this.verifyRefreshToken(userId);


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
