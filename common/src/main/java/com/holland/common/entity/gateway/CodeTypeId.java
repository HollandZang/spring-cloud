package com.holland.common.entity.gateway;

public enum CodeTypeId {
    ROLE;

    public static CodeTypeId find(String key) {
        for (CodeTypeId value : CodeTypeId.values()) {
            if (value.name().equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(CodeTypeId.class, key);
    }
}
