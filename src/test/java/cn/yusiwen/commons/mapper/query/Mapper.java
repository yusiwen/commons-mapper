package cn.yusiwen.commons.mapper.query;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import cn.yusiwen.commons.mapper.BaseMapper;

public interface Mapper extends BaseMapper<User> {

    @Select("select * from users")
    List<User> selectUsers();
}
