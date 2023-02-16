package common.struct.enumeration;

public enum CmdType {
    RESET,
    START,
    STOP,
    PAUSE;

    public static CmdType fromString(String typeStr) {
        for (CmdType type : CmdType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }

}
