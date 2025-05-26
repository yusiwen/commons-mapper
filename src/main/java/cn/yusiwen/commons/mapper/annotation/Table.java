package cn.yusiwen.commons.mapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于表示数据库的表名。
 * <p>
 * 当应用于实体类时，用于指定该类对应的数据库表名，从而可以在进行 ORM (对象关系映射) 时，
 * 将类和表之间建立映射关系。
 * </p>
 *
 * <p>
 * 注解目标：
 * <ul>
 *     <li>仅限于类级别使用。</li>
 * </ul>
 *
 * <p>
 * 保留策略：
 * <ul>
 *     <li>运行时可用（@RetentionPolicy.RUNTIME），允许框架通过反射访问表名。</li>
 * </ul>
 *
 * <p>
 * 使用示例：
 * <pre>
 * &#64;Table("users")
 * public class User {
 *     private Long id;
 *     private String name;
 * }
 * </pre>
 *
 * @author Siwen Yu (yusiwen@gmail.com)
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

    /**
     * 表名
     *
     * @return 表名
     *
     */
    String value();
}
