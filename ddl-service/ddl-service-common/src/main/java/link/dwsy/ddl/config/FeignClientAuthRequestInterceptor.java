package link.dwsy.ddl.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import link.dwsy.ddl.core.constant.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Dwsy
 * @Date 2022/12/8
 */
//@Slf4j
//@Configuration
//public class FeignClientConfiguration {
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return template -> {
//            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            assert requestAttributes != null;
//            HttpServletRequest request = requestAttributes.getRequest();
//            template.header(Constants.JWT_USER_INFO_KEY, request.getHeader(Constants.JWT_USER_INFO_KEY));
//        };
//    }
//
//}

@Component
public class FeignClientAuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        template.header(Constants.JWT_USER_INFO_KEY, request.getHeader(Constants.JWT_USER_INFO_KEY));
    }

}
