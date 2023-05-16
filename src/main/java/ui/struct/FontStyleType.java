package ui.struct;

public enum FontStyleType {
    PLAIN,
    BOLD,
    ITALIC,
    BOLD_ITALIC;

    public static FontStyleType fromString(String typeStr) {
        for (FontStyleType type : FontStyleType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }

    public static FontStyleType fromInt(int typeInt) {
        for (FontStyleType type : FontStyleType.values()) {
            if (type.ordinal() == typeInt) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeInt + " found");
    }
}
