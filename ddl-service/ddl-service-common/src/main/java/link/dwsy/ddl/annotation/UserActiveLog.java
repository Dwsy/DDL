package link.dwsy.ddl.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface UserActiveLog {
//    UserActiveType userActiveType();
//    long sourceId() default 0L;
}
