package common.struct.enumeration;

public enum SensorDataType {
    MSG,
    INC_RESULT,
    INV_REPORT;

    public static SensorDataType fromString(String typeStr) {
        for (SensorDataType type : SensorDataType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
