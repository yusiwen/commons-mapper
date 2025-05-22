package cn.yusiwen.commons.mapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对象属性不映射到数据库注解（可作用于字段上，或类型指定哪些字段不映射）
 *
 * @author Siwen Yu (yusiwen@gmail.com)
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotColumn {
    /**
     * 作用于类型指定不需要映射的字段名数组
     *
     * @return field列表
     */
    String[] fields() default {};
}
