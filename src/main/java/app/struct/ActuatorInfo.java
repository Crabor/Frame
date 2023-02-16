package app.struct;

import com.alibaba.fastjson.JSONObject;

public class ActuatorInfo {
    State state;
    ValueType type;

    public ActuatorInfo(State state, ValueType type) {
        this.state = state;
        this.type = type;
    }

    public ActuatorInfo(JSONObject obj) {
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
        return "ActuatorInfo{" +
                "state=" + state +
                ", type=" + type +
                '}';
    }
}
