package com.holland.common.aggregate;

import com.holland.common.entity.gateway.User;
import org.springframework.beans.BeanUtils;

public class LoginUser extends User {
    private String token;

    public static LoginUser from(User user) {
        final LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user, loginUser);
        return loginUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
