package common.struct;

public enum ServiceType {
    CTX,
    INV;

    public static ServiceType fromString(String typeStr) {
        for (ServiceType type : ServiceType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
