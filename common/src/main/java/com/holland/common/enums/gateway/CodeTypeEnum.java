package com.holland.common.enums.gateway;

public enum CodeTypeEnum {
    ROLE;

    public static CodeTypeEnum find(String key) {
        for (CodeTypeEnum value : CodeTypeEnum.values()) {
            if (value.name().equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(CodeTypeEnum.class, key);
    }
}
