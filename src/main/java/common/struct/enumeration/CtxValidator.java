package common.struct.enumeration;

public enum CtxValidator {
    ECC_IMD,
    ECC_GEAS,
    PCC_IMD,
    PCC_GEAS,
    ConC_IMD,
    ConC_GEAS,
    INFUSE;

    public static CtxValidator fromString(String typeStr) {
        for (CtxValidator type : CtxValidator.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
