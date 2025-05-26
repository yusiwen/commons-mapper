package cn.yusiwen.commons.mapper;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

import cn.yusiwen.commons.mapper.annotation.PrimaryKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 基础实体类，为所有实体类提供通用字段。
 * <p>
 * 该类包含了实体对象的基本属性，如：
 * <ul>
 * <li>主键ID</li>
 * <li>创建时间</li>
 * <li>创建者</li>
 * <li>更新时间</li>
 * <li>更新者</li>
 * </ul>
 */
@Data
@EqualsAndHashCode
@SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN")
public class BaseEntity {

    /**
     * 主键ID
     * <p>
     * 使用 @PrimaryKey 注解标记为数据库主键
     */
    @PrimaryKey
    long id;

    /**
     * 记录创建时间
     */
    LocalDateTime createdTime;

    /**
     * 记录创建者
     */
    String createdBy;

    /**
     * 记录最后更新时间
     */
    LocalDateTime updatedTime;

    /**
     * 记录最后更新者
     */
    String updatedBy;

    /**
     * 构造一个新的基础实体对象。
     * <p>
     * 创建一个空的BaseEntity实例，所有字段将被初始化为默认值：
     * <ul>
     * <li>id: 0</li>
     * <li>createdTime: null</li>
     * <li>createdBy: null</li>
     * <li>updatedTime: null</li>
     * <li>updatedBy: null</li>
     * </ul>
     */
    public BaseEntity() {
        // this constructor is empty
    }

}
