package platform.service.ctx.pattern.types;


public enum FreshnessType {
    TIME,
    NUMBER;

    public static FreshnessType fromString(String resultStr) {
        for (FreshnessType result : FreshnessType.values()) {
            if (result.name().equalsIgnoreCase(resultStr)) {
                return result;
            }
        }

        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }

}
