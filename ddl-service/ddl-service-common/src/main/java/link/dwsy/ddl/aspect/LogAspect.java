package link.dwsy.ddl.aspect;

import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.DTO.LogJson;
import link.dwsy.ddl.annotation.UserActiveLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Dwsy
 * @Date 2022/9/13
 */

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Resource
    HttpServletRequest httpServletRequest;

    @Pointcut("@annotation(link.dwsy.ddl.annotation.UserActiveLog)")
    public void cut() {}

    @Before("cut() && @annotation(userActiveLog)")
    public void doBefore(JoinPoint joinPoint, UserActiveLog userActiveLog) {

        String[] paramNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

        Object[] paramValues = joinPoint.getArgs();

        Map<String, Object> requestParams = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();
            }
            requestParams.put(paramNames[i], value);
        }

        LogJson logJson = LogJson.builder()
                .url(httpServletRequest.getRequestURI())
                .method(httpServletRequest.getMethod())
                .ip(httpServletRequest.getRemoteAddr())
                .requestParams(requestParams)
                .classMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName())
                .build();

        log.info("\nRequest Info      : {}", JSON.toJSONString(logJson));

    }
}
