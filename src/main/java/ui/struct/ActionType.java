package ui.struct;

public enum ActionType {
    DATABASE_SET("DatabaseSet"),
    DATABASE_GET("DatabaseGet"),
    LAYOUT_CHANGE("LayoutChange"),
    ATTRIBUTE_CHANGE("AttributeChange");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActionType fromString(String typeStr) {
        for (ActionType type : ActionType.values()) {
            if (type.name().equalsIgnoreCase(typeStr) || type.getValue().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }

}
