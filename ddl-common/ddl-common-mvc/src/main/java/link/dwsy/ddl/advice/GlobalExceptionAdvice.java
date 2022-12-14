package link.dwsy.ddl.advice;

import com.alibaba.fastjson2.JSONException;
import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/8/15
 * 全局异常捕获处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public R<String> handlerException(
            HttpServletRequest req, Exception ex
    ) {
        R<String> response = new R<>(R.FAIL,"business error","");
        response.setData(ex.getMessage());
        response.setMsg(ex.getMessage());
        log.error("service has error: [{}]", ex.getMessage(), ex);
        return response;
    }

    @ExceptionHandler(value = CodeException.class)
    public R<String> handlerCodeException(HttpServletRequest req, CodeException ex) {
        R<String> response = new R<>(R.FAIL,"business error","");
        response.setMsg(ex.getMessage());
        response.setCode(ex.getCode());
        log.error("service has error: [{}]", ex.getMessage(), ex);
        return response;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> handlerBindException(BindException e) {
        R<String> response = new R<>(R.FAIL,"请求体错误R");
        BindingResult bindingResult = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (bindingResult.hasErrors()) {
            log.error("service has error: [{}]", e.getMessage(), e);
            List<ObjectError> errors = bindingResult.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                FieldError fieldError = (FieldError) errors.get(0);
                response.setMsg(fieldError.getDefaultMessage());
                return response;
            }
        }
        e.printStackTrace();
        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handlerHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        log.error("service has error: [{}]", e.getMessage(), e);
        return new R<String>(R.FAIL,"请求体错误");
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        R<String> response = new R<>(R.FAIL,"请求参数错误");
        BindingResult bindingResult = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                log.error("service has error: [{}]", e.getMessage(), e);
                FieldError fieldError = (FieldError) errors.get(0);
                response.setMsg(fieldError.getDefaultMessage());
                return response;
            }
        }
        e.printStackTrace();
        return response;
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public R<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        e.printStackTrace();
        return new R<>(R.FAIL,"请求参数错误",collect.toString());

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<String> handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        e.printStackTrace();
        return new R<>(R.FAIL,"不支持的HttpMethod:HttpRequestMethodNotSupported");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<String> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        e.printStackTrace();
        return new R<>(R.FAIL,"请求参数错误:MethodArgumentTypeMismatch");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<String> handlerExceptionMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        e.printStackTrace();
        return new R<>(R.FAIL, "请求参数错误:MissingServletRequestParameterException");
    }


    @ExceptionHandler(value = JSONException.class)
    public R<String> handlerJSONException() {
        return new R<>(R.FAIL, "JSON格式错误");
    }

    @ExceptionHandler(value = JsonProcessingException.class)
    public R<String> handlerJsonProcessingException(JsonProcessingException e) {
        e.printStackTrace();
        return new R<>(R.FAIL, "JsonProcessingException");
    }
}
