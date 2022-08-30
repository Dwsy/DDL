package link.dwsy.ddl.aspect;

import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Component
@Aspect
public class AuthAspect {
    @Resource
    private UserSupport userSupport;

    @Pointcut("@annotation(link.dwsy.ddl.annotation.AuthAnnotation)")
    public void cut() {
    }


    @Before("cut() && @annotation(authAnnotation)")
    public void doBefore(JoinPoint joinPoint, AuthAnnotation authAnnotation) throws Throwable {
        LoginUserInfo loginUserInfo = userSupport.getCurrentUser();
        int level = loginUserInfo.getLevel();
        if (level < authAnnotation.Level()) {
            throw new CodeException(CustomerErrorCode.UserLevelLow);
        }
    }
}
