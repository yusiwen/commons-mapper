package cn.yusiwen.commons.mapper;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类，提供常用的字符串处理方法。
 *
 * @author Siwen Yu (yusiwen@gmail.com)
 */
public class StrUtil {

    /**
     * 私有构造方法，禁止实例化该工具类。
     */
    private StrUtil() {}

    /**
     * 截取字符串的子串，长度不超过指定的最大值。
     *
     * @param str 原始字符串，可以为 null。
     * @param max 子串的最大长度，不能为负。
     * @return 如果输入字符串为 null，返回 null；否则返回截取后的子串。
     */
    public static String substr(String str, int max) {
        return Optional.ofNullable(str).map(s -> s.substring(0, Math.min(max, s.length()))).orElse(null);
    }

    /**
     * 驼峰模式字符串转换为下划线字符串
     *
     * @param camelStr 驼峰字符串
     * @return 下划线字符串
     */
    public static String camel2Underscore(String camelStr) {
        return convertCamel(camelStr, '_');
    }

    /**
     * 转换驼峰字符串为指定分隔符的字符串 <br/>
     * 如：camelStr:"UserInfo" separator:'_' <br/>
     * return "user_info"
     *
     * @param camelStr 驼峰字符串
     * @param separator 分隔符
     * @return 将驼峰字符串转换后的字符串
     */
    public static String convertCamel(String camelStr, char separator) {
        if (StringUtils.isBlank(camelStr)) {
            return camelStr;
        }
        StringBuilder out = new StringBuilder();
        char[] strChar = camelStr.toCharArray();
        for (int i = 0, len = strChar.length; i < len; i++) {
            char c = strChar[i];
            if (Character.isUpperCase(c)) {
                // 如果不是首字符，则需要添加分隔符
                if (i != 0) {
                    out.append(separator);
                }
                out.append(Character.toLowerCase(c));
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }

    /**
     * 尝试将字符串解析为Long类型的数值
     *
     * @param str 要解析的字符串
     * @return 如果解析成功，返回包含Long值的Optional对象；如果解析失败，返回空的Optional对象
     */
    public static Optional<Long> parseLong(String str) {
        try {
            return Optional.of(Long.valueOf(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
