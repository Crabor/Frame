package common.struct;

public enum SensorModeType {
    ACTIVE,
    PASSIVE;

    public static SensorModeType fromString(String typeStr) {
        for (SensorModeType type : SensorModeType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
