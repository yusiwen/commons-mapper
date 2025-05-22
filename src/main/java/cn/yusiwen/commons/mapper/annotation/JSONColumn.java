package cn.yusiwen.commons.mapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于标记类或字段，以指定它们在 JSON 映射中的特殊处理方式。
 * 适用于实体字段或类，用于声明与 JSON 数据的映射关系。
 *
 * <p>
 * 使用示例：
 * <pre>
 * {@code @JSONColumn(fields} = {"field1", "field2"})
 * private String exampleField;
 * </pre>
 * </p>
 *
 * <p>
 * 注解参数：
 * <ul>
 *     <li>fields：需要映射的 JSON 字段的名称数组。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 注解目标：
 * <ul>
 *     <li>可以应用于类级别或字段级别。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 运行时保留策略：注解信息会保留到运行时，可通过反射获取。
 * </p>
 *
 * @author Siwen Yu(yusiwen@gmail.com)
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSONColumn {

    String[] fields() default {};
}
