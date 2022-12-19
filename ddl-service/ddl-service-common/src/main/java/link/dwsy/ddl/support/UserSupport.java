package link.dwsy.ddl.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.core.constant.Constants;
import link.dwsy.ddl.core.constant.TokenConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserSupport {

    @Value("${spring.cloud.nacos.discovery.enabled}")
    private boolean isCloud;


    //    cloud模式优先从header中获取loginUserInfo boot模式获取token
//    todo 缓存解析结果
    public LoginUserInfo getCurrentUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        request.getHeader(Constants.JWT_USER_INFO_KEY);
        if (isCloud) {
            String loginUserInfoString = request.getHeader(Constants.JWT_USER_INFO_KEY);
//            log.info("loginUserInfoString{}",loginUserInfoString);
            if (StrUtil.isBlank(loginUserInfoString)) {
                return null;
            }
            //            this.loginUserInfoCache=loginUserInfo;
            return JSON.parseObject(loginUserInfoString, LoginUserInfo.class);
        } else {
            String token = request.getHeader(TokenConstants.AUTHENTICATION);
            if (StrUtil.isBlank(token)) {
                return null;
            }
//            String[] t = token.split(" ");
            return TokenUtil.parseUserInfoFromToken(token);
        }
    }

    public String getUserAgent() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            return request.getHeader("User-Agent");
        } else {
            return "no-user-agent";
        }

    }

    private void verifyRefreshToken(Long userId) {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//        String refreshToken = requestAttributes.getRequest().getHeader("refreshToken");
//        String dbRefreshToken = userService.getRefreshTokenByUserId(userId);
//        if(!dbRefreshToken.equals(refreshToken)){
//            throw new ConditionException("非法用户！");
//        }
    }


    public String getIpv4() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            String unknown = "unknown";
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ip != null && ip.length() > 0) {
                String[] ips = ip.split(",");
                if (ips.length > 0) {
                    ip = ips[0];
                }
            }
            return ip;
        } else {
            return null;
        }

    }
}
