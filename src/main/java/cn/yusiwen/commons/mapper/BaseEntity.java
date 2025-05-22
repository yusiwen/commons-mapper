package cn.yusiwen.commons.mapper;

import cn.yusiwen.commons.mapper.annotation.PrimaryKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 基础实体类，为所有实体类提供通用字段。
 * <p>
 * 该类包含了实体对象的基本属性，如：
 * <ul>
 *     <li>主键ID</li>
 *     <li>创建时间</li>
 *     <li>创建者</li>
 *     <li>更新时间</li>
 *     <li>更新者</li>
 * </ul>
 * </p>
 */
@Data
@EqualsAndHashCode
@SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN")
public class BaseEntity {

    /**
     * 主键ID
     * <p>
     * 使用 @PrimaryKey 注解标记为数据库主键
     * </p>
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

}