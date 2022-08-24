package link.dwsy.ddl.advice;

import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Dwsy
 * @Date 2022/8/15
 * 全局异常捕获处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public R<String> handlerCommerceException(
            HttpServletRequest req, Exception ex
    ) {
        //TODO 后期细分异常 错误代码
        R<String> response = new R<>(R.FAIL,"business error","");
        response.setData(ex.getMessage());
        response.setMsg(ex.getMessage());
        log.error("commerce service has error: [{}]", ex.getMessage(), ex);
        return response;
    }

    @ExceptionHandler(value = CodeException.class)
    public R<String> CodeException(HttpServletRequest req, CodeException ex) {
        //TODO 后期细分异常 错误代码
        R<String> response = new R<>(R.FAIL,"business error","");
        response.setMsg(ex.getMessage());
        response.setCode(ex.getCode());
        log.error("commerce service has error: [{}]", ex.getMessage(), ex);
        return response;
    }
}
