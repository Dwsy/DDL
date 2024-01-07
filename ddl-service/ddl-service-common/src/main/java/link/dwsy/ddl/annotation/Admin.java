package link.dwsy.ddl.annotation;

import java.lang.annotation.*;

/**
 * @Author Dwsy
 * @Date 2023/3/31
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Admin {
}
