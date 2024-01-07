package link.dwsy.ddl.annotation;

import java.lang.annotation.*;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface AuthAnnotation {
    int Level() default 0;

}
