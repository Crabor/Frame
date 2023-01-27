package app.struct;

public class SensorInfo {
    State state;
    ValueType type;

    public SensorInfo(State state, ValueType type) {
        this.state = state;
        this.type = type;
    }
}
