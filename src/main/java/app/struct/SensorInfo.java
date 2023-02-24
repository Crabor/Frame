package app.struct;

import com.alibaba.fastjson.JSONObject;
import common.struct.State;

public class SensorInfo {
    public State state;
    public ValueType type;

    public SensorInfo(State state, ValueType type) {
        this.state = state;
        this.type = type;
    }

    public SensorInfo(JSONObject obj) {
        this.state = State.fromString(obj.getString("state"));
        this.type = ValueType.fromString(obj.getString("value_type"));
    }

    @Override
    public String toString() {
        return "SensorInfo{" +
                "state=" + state +
                ", type=" + type +
                '}';
    }
}
