package link.dwsy.ddl.annotation;

import link.dwsy.ddl.XO.Enum.User.PointsType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Points {
   PointsType TYPE();
}
