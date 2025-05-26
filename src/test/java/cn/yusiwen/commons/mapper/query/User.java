package cn.yusiwen.commons.mapper.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.yusiwen.commons.mapper.BaseEntity;
import cn.yusiwen.commons.mapper.annotation.Table;

@Setter
@Getter
@ToString
@Table("users")
public class User extends BaseEntity {

    private String name;

}
