package com.holland.common.enums.gateway;

public enum RoleEnum {
    TOKEN,GUEST,ADMIN;

    public static RoleEnum find(String key) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.name().equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(RoleEnum.class, key);
    }
}
