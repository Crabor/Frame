package common.struct.enumeration;

public enum SensorMode {
    ACTIVE,
    PASSIVE;

    public static SensorMode fromString(String typeStr) {
        for (SensorMode type : SensorMode.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
