package com.holland.common.spring;

import com.holland.common.enums.gateway.RoleEnum;

import java.util.HashMap;
import java.util.List;

public class AuthCheckMapping extends HashMap<String, List<RoleEnum>> {
    public AuthCheckMapping(int initialCapacity) {
        super(initialCapacity);
    }
}
