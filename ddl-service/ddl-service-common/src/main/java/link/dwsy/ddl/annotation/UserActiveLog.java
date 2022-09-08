package link.dwsy.ddl.annotation;

import link.dwsy.ddl.XO.Enum.UserActiveType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Component
public @interface UserActiveLog {
    UserActiveType userActiveType();
    long sourceId() default 0L;
}
