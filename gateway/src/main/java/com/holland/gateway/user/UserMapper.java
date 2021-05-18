package com.holland.gateway.user;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Insert("insert into \"user\"(login_name, password, create_time, update_time) values(#{loginName}, #{password}, #{createTime}, #{updateTime})")
    int insert(User user);

    @UpdateProvider(type = UserSqlFactory.class, method = "update")
    int update(User user);

    @Select("select id, login_name, password ,create_time, update_time from \"user\" where login_name = #{loginName}")
    Optional<User> getByLoginName(String loginName);

    class UserSqlFactory {

        public String update(User user) {
            final SQL sql = new SQL()
                    .UPDATE("\"user\"");

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                sql.SET("\"password\" = '" + user.getPassword() + "'");
            }

            sql.SET("\"update_time\" = '" + user.getUpdateTime() + "'");

            return sql.WHERE("\"id\" = '" + user.getId() + "'")
                    .toString();
        }
    }
}
