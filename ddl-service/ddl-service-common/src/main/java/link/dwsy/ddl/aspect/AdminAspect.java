package link.dwsy.ddl.aspect;

import link.dwsy.ddl.annotation.Admin;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2023/3/31
 */

@Component
@Aspect
@Slf4j
public class AdminAspect {
    @Resource
    private UserSupport userSupport;
    @Resource
    private UserRepository userRepository;

    @Pointcut("@annotation(link.dwsy.ddl.annotation.Admin)")
    public void cut() {
    }


    @Before("cut() && @annotation(admin)")
    public void doBefore(JoinPoint joinPoint, Admin admin) {
        LoginUserInfo user = userSupport.getCurrentUser();
        Long userId = user.getId();
        if (!userRepository.existsByIdAndIsAdminTrue(userId)) {
            throw new CodeException(CustomerErrorCode.UserNotAdmin);
        }

    }
}
