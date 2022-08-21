package link.dwsy.ddl.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.core.domain.R;
import link.dwsy.ddl.annotation.IgnoreResponseAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author Dwsy
 * @Date 2022/8/15
 */
@RestControllerAdvice(value = "link.dwsy.ddl")
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
    @Autowired
    private ObjectMapper objectMapper;
    /**
     * <h2>判断是否需要对响应进行处理</h2>
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        return true;
    }

    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        // 定义最终的返回对象
        R<Object> response = new R<>();
        response.setCode(R.SUCCESS);
        response.setMsg("");

        //这段代码一定要加，如果Controller直接返回String的话，
        //SpringBoot是直接返回，故我们需要手动转换成json。
        if(o instanceof String){
            response.setData(o);
            try {
                return objectMapper.writeValueAsString(response);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (null == o) {
            return response;
        } else if (o instanceof R) {
            response = (R<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
