package link.dwsy.ddl.annotation;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

/**
 * 自定义注解接收单个参数
 *
 * @author created by wutong li on 2021/04/29
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestSingleParam {

    String value();

    boolean required() default true;

    String defaultValue() default ValueConstants.DEFAULT_NONE;
}