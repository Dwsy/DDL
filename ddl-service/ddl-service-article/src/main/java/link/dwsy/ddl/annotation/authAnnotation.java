package link.dwsy.ddl.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Component
public @interface authAnnotation {
    int Level() default 1;
}
