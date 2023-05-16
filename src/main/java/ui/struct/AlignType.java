package ui.struct;

public enum AlignType {
    CENTER(10),
    NORTH(11),
    NORTHWEST(12),
    EAST(13),
    SOUTHEAST(14),
    SOUTH(15),
    SOUTHWEST(16),
    WEST(17),
    NORTHEAST(18);


    private final int value;

    private AlignType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlignType fromString(String alignStr) {
        for (AlignType align : AlignType.values()) {
            if (align.name().equalsIgnoreCase(alignStr)) {
                return align;
            }
        }
        throw new IllegalArgumentException("No constant with text " + alignStr + " found");
    }

    public static AlignType fromInt(int value) {
        for (AlignType align : AlignType.values()) {
            if (align.getValue() == value) {
                return align;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }
}
