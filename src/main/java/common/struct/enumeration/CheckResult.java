package common.struct.enumeration;

public enum CheckResult {
    INV_GENERATING,
    INV_VIOLATED,
    INV_PASSED;

    public static CheckResult fromString(String resultStr) {
        for (CheckResult result : CheckResult.values()) {
            if (result.name().equalsIgnoreCase(resultStr)) {
                return result;
            }
        }
        throw new IllegalArgumentException("No constant with text " + resultStr + " found");
    }
}
