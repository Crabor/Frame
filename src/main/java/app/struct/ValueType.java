package app.struct;

public enum ValueType {
    STRING,
    INT,
    DOUBLE;

    public static ValueType fromString(String str) {
        for (ValueType type : ValueType.values()) {
            if (type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + str + " found");
    }
}
