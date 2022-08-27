package link.dwsy.ddl.advice;

import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST) //设置状态码为 400
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        R<String> response = new R<>(R.FAIL,"请求参数错误","");
        BindingResult exceptions = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                FieldError fieldError = (FieldError) errors.get(0);
                response.setMsg(fieldError.getDefaultMessage());
                return response;
            }
        }
        response.setMsg("请求参数错误");
        return response;
    }
}
