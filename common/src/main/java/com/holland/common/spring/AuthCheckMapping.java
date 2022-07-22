package com.holland.common.spring;

import java.util.HashMap;
import java.util.List;

public class AuthCheckMapping extends HashMap<String, List<AuthCheck.AuthCheckEnum>> {
    public AuthCheckMapping(int initialCapacity) {
        super(initialCapacity);
    }
}
