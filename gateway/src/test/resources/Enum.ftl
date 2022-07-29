package ${package};

public enum ${class} {
    ${valueStr}

    public static ${class} find(String key) {
        for (${class} value : ${class}.values()) {
            if (value.name().equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(${class}.class, key);
    }
}
