package common.struct.enumeration;

public enum ServiceType {
    CTX,
    INV,
    ALL;

    public static ServiceType fromString(String typeStr) {
        for (ServiceType type : ServiceType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
