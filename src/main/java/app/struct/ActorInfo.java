package app.struct;

import com.alibaba.fastjson.JSONObject;
import common.struct.State;

public class ActorInfo {
    public State state;
    public ValueType type;

    public ActorInfo(State state, ValueType type) {
        this.state = state;
        this.type = type;
    }

    public ActorInfo(JSONObject obj) {
        this.state = State.fromString(obj.getString("state"));
        this.type = ValueType.fromString(obj.getString("value_type"));
    }

    @Override
    public String toString() {
        return "ActorInfo{" +
                "state=" + state +
                ", type=" + type +
                '}';
    }
}
