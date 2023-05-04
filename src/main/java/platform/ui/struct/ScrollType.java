package platform.ui.struct;

public enum ScrollType {
    VERTICAL,
    HORIZONTAL,
    BOTH;

    public static ScrollType fromString(String typeStr) {
        for (ScrollType type : ScrollType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
