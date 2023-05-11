package ui.struct;

public enum PropertyType {
    SCROLL("scroll"),
    BACKGROUND("background"),
    FONT("font"),
    VISIBLE("visible"),
    SIZE("size"),
    TITLE("title"),
    TEXT("text"),
    COLUMN_NAMES("column_names"),
    COLUMN_WIDTH("column_width"),
    ROW_HEIGHT("row_height"),
    EDITABLE("editable"),
    DIRS("dirs"),
    CONTENT("content");

    private String value;

    PropertyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PropertyType fromString(String value) {
        for (PropertyType propertyType : PropertyType.values()) {
            if (propertyType.getValue().equals(value) || propertyType.name().equals(value)) {
                return propertyType;
            }
        }
        throw new IllegalArgumentException("No such property type: " + value);
    }
}
