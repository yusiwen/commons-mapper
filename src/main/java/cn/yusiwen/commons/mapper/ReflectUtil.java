package cn.yusiwen.commons.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 反射工具类，提供对类、字段和方法的反射操作功能。
 * <p>
 * 主要功能包括:
 * <ul>
 * <li>获取类的字段(包括父类继承的字段)</li>
 * <li>获取和设置字段值</li>
 * <li>调用对象方法</li>
 * <li>检查方法类型(equals/hashCode/toString等)</li>
 * </ul>
 *
 * @author Siwen Yu (yusiwen@gmail.com)
 * @since 1.0
 */
public final class ReflectUtil {

    private ReflectUtil() {}

    /**
     * 获取指定类的所有字段，包含父类继承的字段。
     *
     * @param clazz 要获取字段的类
     * @return 类的所有字段数组
     */
    public static Field[] getFields(Class<?> clazz) {
        return getFields(clazz, null);
    }

    /**
     * 获取指定类的所有的field,包括父类
     *
     * @param clazz 字段所属类型
     * @param fieldFilter 字段过滤器
     * @return 符合过滤器条件的字段数组
     */
    public static Field[] getFields(Class<?> clazz, Predicate<Field> fieldFilter) {
        List<Field> fields = new ArrayList<>(32);
        while (Object.class != clazz && clazz != null) {
            // 获得该类所有声明的字段，即包括public、private和protected，但是不包括父类的申明字段，
            // getFields：获得某个类的所有的公共（public）的字段，包括父类中的字段
            for (Field field : clazz.getDeclaredFields()) {
                if (fieldFilter != null && !fieldFilter.test(field)) {
                    continue;
                }
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 对指定类的所有字段执行consumer操作
     *
     * @param clazz 目标对象
     * @param consumer 对字段进行操作
     */
    public static void doWithFields(Class<?> clazz, Consumer<Field> consumer) {
        Arrays.stream(getFields(clazz)).forEach(consumer);
    }

    /**
     * 获取指定类中指定名称的字段，包括父类中的字段。
     *
     * @param clazz 要查找的类
     * @param name 字段名称
     * @return 找到的字段对象，如果未找到返回null
     */
    public static Field getField(Class<?> clazz, String name) {
        return getField(clazz, name, null);
    }

    /**
     * 获取指定类的指定field,包括父类
     *
     * @param clazz 字段所属类型
     * @param name 字段名
     * @param type field类型
     * @return Field对象
     */
    public static Field getField(Class<?> clazz, String name, Class<?> type) {
        while (clazz != Object.class && clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * 获取字段值
     *
     * @param field 字段
     * @param target 字段所属实例对象
     * @return 字段值
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    public static Object getFieldValue(Field field, Object target) {
        makeAccessible(field);
        try {
            return field.get(target);
        } catch (Exception e) {
            throw new IllegalStateException(
                String.format("获取%s对象的%s字段值错误!", target.getClass().getName(), field.getName()), e);
        }
    }

    /**
     * 获取对象中指定field值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (ObjectUtil.isWrapperOrPrimitive(obj)) {
            return obj;
        }
        return getFieldValue(getField(obj.getClass(), fieldName), obj);
    }

    /**
     * 获取指定对象中指定字段路径的值(类似js访问对象属性) <br/>
     * 如：Product p = new Product(new User()) <br/>
     * 可使用ReflectionUtils.getValueByFieldPath(p, "user.name")获取到用户的name属性
     *
     * @param obj 取值对象
     * @param fieldPath 字段路径(形如 user.name)
     * @return 字段value
     */
    public static Object getValueByFieldPath(Object obj, String fieldPath) {
        String[] fieldNames = fieldPath.split("\\.");
        Object result = null;
        for (String fieldName : fieldNames) {
            result = getFieldValue(obj, fieldName);
            if (result == null) {
                return null;
            }
            obj = result;
        }
        return result;
    }

    /**
     * 设置字段值
     *
     * @param field 字段
     * @param target 字段所属对象实例
     * @param value 需要设置的值
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    public static void setFieldValue(Field field, Object target, Object value) {
        makeAccessible(field);
        try {
            field.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException(
                String.format("设置%s对象的%s字段值错误!", target.getClass().getName(), field.getName()), e);
        }
    }

    /**
     * 设置字段为可访问，主要用于访问私有字段。
     *
     * @param field 要设置的字段对象
     */
    @SuppressFBWarnings("RFI_SET_ACCESSIBLE")
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
            || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 调用对象的无参数方法。
     *
     * @param method 要调用的方法对象
     * @param target 目标对象实例
     * @return 方法执行的返回结果
     */
    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, new Object[0]);
    }

    /**
     * 调用指定对象的方法
     *
     * @param method 方法对象
     * @param target 调用对象
     * @param args 方法参数
     * @return 执行结果
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            makeAccessible(method);
            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new IllegalStateException(
                String.format("执行%s.%s()方法错误!", target.getClass().getName(), method.getName()), ex);
        }
    }

    /**
     * 设置方法可见性
     *
     * @param method 方法
     */
    @SuppressFBWarnings("RFI_SET_ACCESSIBLE")
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
            && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 判断给定的方法是否为Object类的equals方法。
     *
     * @param method 要检查的方法
     * @return 如果是equals方法返回true，否则返回false
     * @see Object#equals(Object)
     */
    public static boolean isEqualsMethod(Method method) {
        if (!"equals".equals(method.getName())) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return paramTypes.length == 1 && paramTypes[0] == Object.class;
    }

    /**
     * 判断给定的方法是否为Object类的hashCode方法。
     *
     * @param method 要检查的方法
     * @return 如果是hashCode方法返回true，否则返回false
     * @see Object#hashCode()
     */
    public static boolean isHashCodeMethod(Method method) {
        return "hashCode".equals(method.getName()) && method.getParameterCount() == 0;
    }

    /**
     * 判断给定的方法是否为Object类的toString方法。
     *
     * @param method 要检查的方法
     * @return 如果是toString方法返回true，否则返回false
     * @see Object#toString()
     */
    public static boolean isToStringMethod(Method method) {
        return "toString".equals(method.getName()) && method.getParameterCount() == 0;
    }
}
