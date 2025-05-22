package cn.yusiwen.commons.mapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于标记字段是数据库中的主键。
 * <p>
 * 应用于数据库实体类的字段时，框架或工具可能通过该注解识别主键字段，并在
 * 数据库操作（例如插入、更新或删除）中给予特殊处理。
 * </p>
 *
 * <p>
 * 注解目标：
 * <ul>
 *     <li>仅限字段级别使用。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 保留策略：
 * <ul>
 *     <li>运行时可用（@RetentionPolicy.RUNTIME），可通过反射机制在运行时获取注解信息。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>
 * public class User {
 *     &#64;PrimaryKey
 *     private Long id;
 * }
 * </pre>
 * </p>
 *
 * @author Siwen Yu (yusiwen@gmail.com)
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrimaryKey {
}
