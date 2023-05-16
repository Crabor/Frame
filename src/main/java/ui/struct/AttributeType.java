package ui.struct;

import java.awt.*;

public enum AttributeType {
    //Single
    SCROLL("scroll"),
    BACKGROUND("background"),
    VISIBLE("visible"),
    TITLE("title"),
    TEXT("text"),
    COLUMN_WIDTH("column_width"),
    ROW_HEIGHT("row_height"),
    EDITABLE("editable"),
    //List
    SIZE("size"),
    FONT("font"),
    COLUMN_NAMES("column_names"),
    DIRS("dirs"),
    POSITION("position"),
    //Matrix
    CONTENT("content");

    private final String value;

    AttributeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AttributeType fromString(String value) {
        for (AttributeType attributeType : AttributeType.values()) {
            if (attributeType.getValue().equals(value) || attributeType.name().equals(value)) {
                return attributeType;
            }
        }
        throw new IllegalArgumentException("No such property type: " + value);
    }
}
