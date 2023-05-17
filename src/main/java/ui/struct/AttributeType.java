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
    SELECTED_ITEM("selected_item"),
    //List
    SIZE("size"),
    FONT("font"),
    COLUMN_NAMES("column_names"),
    DIRS("dirs"),
    POSITION("position"),
    USER_VALS("user_vals"),//这个属性和UI显示无关，用于存储用户变量
    SELECTED_PATH("selected_path"),
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
