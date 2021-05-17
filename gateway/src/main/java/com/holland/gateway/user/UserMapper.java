package com.holland.gateway.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Insert("insert into " + UserSqlFactory.TABLE_NAME + "(login_name, password) values(#{loginName}, #{password})")
    int insert(User user);

    @UpdateProvider(type = UserSqlFactory.class, method = "update")
    int update(User user);

    @Select("select id, login_name, password from " + UserSqlFactory.TABLE_NAME + " where login_name = #{loginName}")
    Optional<User> getByLoginName(String loginName);
}

class UserSqlFactory {

    static final String TABLE_NAME = "\"config\".\"public\".\"user\"";

    public String update(User user) {
        SQL sql = new SQL()
                .UPDATE(TABLE_NAME);

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql.SET("\"password\" = '" + user.getPassword() + "'");
        }

        return sql.WHERE("\"id\" = '" + user.getId() + "'")
                .toString();
    }

}