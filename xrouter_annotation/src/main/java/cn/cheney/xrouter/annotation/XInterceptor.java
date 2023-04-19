package cn.cheney.xrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface XInterceptor {

    String[] paths() default {};

    String[] modules() default {};

    /**
     * 优先级 值越大 越先触发
     */
    int priority() default 0;
}
