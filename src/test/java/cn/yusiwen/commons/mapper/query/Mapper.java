package cn.yusiwen.commons.mapper.query;

import cn.yusiwen.commons.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface Mapper extends BaseMapper<User> {

    @Select("select * from users")
    List<User> selectUsers();
}
