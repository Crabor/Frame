package common.struct;

public enum State {
    ON,
    OFF;

    public static State fromString(String str) {
        for (State type : State.values()) {
            if (type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + str + " found");
    }
}
