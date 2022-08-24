package link.dwsy.ddl.aspect;

import link.dwsy.ddl.annotation.authAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.User;
import link.dwsy.ddl.repository.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Component
@Aspect
public class authAspect {
    @Resource
    private UserSupport userSupport;

    @Resource
    private UserRepository userRepository;
    @Pointcut("@annotation(link.dwsy.ddl.annotation.authAnnotation)")
    public void cut() {}


    @Before("cut() && @annotation(authAnnotation)")
    public void doBefore(JoinPoint joinPoint, authAnnotation authAnnotation) throws Throwable {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        Long userId = currentUser.getId();
        User user = userRepository.findUserByIdAndDeletedIsFalse(userId);
        if (user.getLevel()<authAnnotation.Level()) {
            throw new CodeException(4, "权限不足");
        }
    }
}
