package common.struct.enumeration;

public enum DeviceType {
    SENSOR,
    ACTOR,
    HYBRID;

    public static DeviceType fromString(String type) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.name().equalsIgnoreCase(type)) {
                return deviceType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + type + " found");
    }
}
