package ui.struct;

public enum ThemeType {
    //Light themes
    BUSINESS("Business"),
    BUSINESS_BLUE_STEEL("BusinessBlueSteel"),
    BUSINESS_BLACK_STEEL("BusinessBlackSteel"),
    CREME("Creme"),
    CREME_COFFEE("CremeCoffee"),
    SAHARA("Sahara"),
    MODERATE("Moderate"),
    NEBULA("Nebula"),
    NEBULA_AMETHYST("NebulaAmethyst"),
    NEBULA_BRICK_WALL("NebulaBrickWall"),
    AUTUMN("Autumn"),
    MIST_SILVER("MistSilver"),
    MIST_AQUA("MistAqua"),
    DUST("Dust"),
    DUST_COFFEE("DustCoffee"),
    GEMINI("Gemini"),
    MARINER("Mariner"),
    SENTINEL("Sentinel"),
    CERULEAN("Cerulean"),
    GREEN_MAGIC("GreenMagic"),
    //Dark themes
    TWILIGHT("Twilight"),
    NIGHT_SHADE("NightShade"),
    MAGELLAN("Magellan"),
    GRAPHITE("Graphite"),
    GRAPHITE_CHALK("GraphiteChalk"),
    GRAPHITE_AQUA("GraphiteAqua"),
    GRAPHITE_ELECTRIC("GraphiteElectric"),
    GRAPHITE_GOLD("GraphiteGold"),
    GRAPHITE_SIENNA("GraphiteSienna"),
    GRAPHITE_SUNSET("GraphiteSunset"),
    GRAPHITE_GLASS("GraphiteGlass"),
    RAVEN("Raven");

    private final String value;

    ThemeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ThemeType fromString(String typeStr) {
        for (ThemeType type : ThemeType.values()) {
            if (type.name().equalsIgnoreCase(typeStr) || type.getValue().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
