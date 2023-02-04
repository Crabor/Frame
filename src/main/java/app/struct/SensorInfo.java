package app.struct;

import com.alibaba.fastjson.JSONObject;

public class SensorInfo {
    State state;
    ValueType type;

    public SensorInfo(State state, ValueType type) {
        this.state = state;
        this.type = type;
    }

    public SensorInfo(JSONObject obj) {
        if (obj.getString("state").equalsIgnoreCase("on")) {
            this.state = State.ON;
        } else {
            this.state = State.OFF;
        }

        String valueType = obj.getString("value_type");
        if (valueType.equalsIgnoreCase("string")) {
            this.type = ValueType.STRING;
        } else if (valueType.equalsIgnoreCase("int")) {
            this.type = ValueType.INT;
        } else if (valueType.equalsIgnoreCase("double")) {
            this.type = ValueType.DOUBLE;
        }
    }

    @Override
    public String toString() {
        return "SensorInfo{" +
                "state=" + state +
                ", type=" + type +
                '}';
    }
}
