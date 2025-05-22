package cn.yusiwen.commons.mapper;

import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 对象工具类，提供对象类型判断、默认值处理等常用工具方法。
 *
 * <p>此工具类包含以下功能：
 * <ul>
 *   <li>对象类型判断 - 判断对象是否为集合、Map、数字等类型</li>
 *   <li>默认值处理 - 在对象为null时返回默认值</li>
 *   <li>基本类型判断 - 判断对象是否为基本类型或其数组</li>
 * </ul>
 * </p>
 *
 * @author Siwen Yu (yusiwen@gmail.com)
 * @since 1.0
 */
public class ObjectUtil {

    private ObjectUtil() {
    }

    /**
     * 如果obj为null，则返回默认值，不为null，则返回obj
     *
     * @param obj          obj
     * @param defaultValue 默认值
     * @param <T>          值泛型
     * @return obj不为null 返回obj，否则返回默认值
     */
    public static <T> T defaultIfNull(T obj, T defaultValue) {
        return obj != null ? obj : defaultValue;
    }

    //---------------------------------------------------------------------
    // 对象类型判断
    //---------------------------------------------------------------------

    /**
     * 判断对象是否为集合类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是Collection类型返回true，否则返回false
     */
    public static boolean isCollection(Object obj) {
        return obj instanceof Collection;
    }

    /**
     * 判断对象是否为Map类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是Map类型返回true，否则返回false
     */
    public static boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    /**
     * 判断对象是否为数字类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是Number类型返回true，否则返回false
     */
    public static boolean isNumber(Object obj) {
        return obj instanceof Number;
    }

    /**
     * 判断对象是否为布尔类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是Boolean类型返回true，否则返回false
     */
    public static boolean isBoolean(Object obj) {
        return obj instanceof Boolean;
    }

    /**
     * 判断对象是否为枚举类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是Enum类型返回true，否则返回false
     */
    public static boolean isEnum(Object obj) {
        return obj instanceof Enum;
    }

    /**
     * 判断对象是否为日期类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是Date或TemporalAccessor类型返回true，否则返回false
     */
    public static boolean isDate(Object obj) {
        return obj instanceof Date || obj instanceof TemporalAccessor;
    }

    /**
     * 判断对象是否为字符序列类型
     *
     * @param obj 要判断的对象
     * @return 如果对象是CharSequence类型返回true，否则返回false
     */
    public static boolean isCharSequence(Object obj) {
        return obj instanceof CharSequence;
    }

    /**
     * 判断对象是否为八大基本类型包装类除外即(boolean, byte, char, short, int, long, float, and double)<br/>
     *
     * @param obj Object
     * @return 判断结果
     */
    public static boolean isPrimitive(Object obj) {
        return obj != null && obj.getClass().isPrimitive();
    }

    /**
     * 判断对象是否为包装类或者非包装类的基本类型
     *
     * @param obj Object
     * @return 判断结果
     */
    public static boolean isWrapperOrPrimitive(Object obj) {
        return isPrimitive(obj) || isNumber(obj) || isCharSequence(obj) || isBoolean(obj);
    }

    /**
     * 判断一个对象是否为数组
     *
     * @param obj Object
     * @return 判断结果
     */
    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    /**
     * 判断一个对象是否为基本类型数组即(int[], long[], boolean[], double[]....)
     *
     * @param obj Object
     * @return 判断结果
     */
    public static boolean isPrimitiveArray(Object obj) {
        return isArray(obj) && obj.getClass().getComponentType().isPrimitive();
    }
}
