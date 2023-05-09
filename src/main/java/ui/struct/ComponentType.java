package ui.struct;

public enum ComponentType {
    WINDOW,
    PANEL,
    LABEL,
    TEXTFIELD,
    BUTTON,
    CHECKBOX,
    COMBOBOX,
    TABLE,
    LIST,
    TREE,
    BARCHART,
    PIECHART,
    LINECHART,
    IMAGE;

    public static ComponentType fromString(String typeStr) {
       for (ComponentType type : ComponentType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
