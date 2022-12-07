package link.dwsy.ddl.aspect;

import link.dwsy.ddl.XO.VO.LevelAndExperienceVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.constant.LevelConstants;
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
 * @Date 2022/8/24
 */
@Component
@Aspect
@Slf4j
public class AuthAspect {
    @Resource
    private UserSupport userSupport;
    @Resource
    private UserRepository userRepository;

    @Pointcut("@annotation(link.dwsy.ddl.annotation.AuthAnnotation)")
    public void cut() {
    }


    @Before("cut() && @annotation(authAnnotation)")
    public void doBefore(JoinPoint joinPoint, AuthAnnotation authAnnotation) {
        LoginUserInfo loginUserInfo = userSupport.getCurrentUser();
        if (loginUserInfo == null) {
            throw new CodeException(CustomerErrorCode.UserNotLogin);
        }
//        int level = loginUserInfo.getLevel();
        LevelAndExperienceVO levelAndExperienceVO = userRepository.getUserLevelAndExperience(loginUserInfo.getId());
        int level = LevelConstants.getUserLevelByexperience(levelAndExperienceVO.getExperience());
        if (level < authAnnotation.Level()) {
            throw new CodeException(CustomerErrorCode.UserLevelLow);
        } else {
            if (levelAndExperienceVO.getLevel() != level) {
                userRepository.updateUserLevel(loginUserInfo.getId(),level);
                log.info("用户{}等级{}更新成功",loginUserInfo.getId(),level);
            }
        }

    }
}
