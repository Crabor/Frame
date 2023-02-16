package common.struct;

import java.util.HashMap;
import java.util.Map;

public class SensorData {
    Map<String, Object> data;

    public SensorData() {
        data = new HashMap<>();
    }

    public Object get(String name) {
        return data.get(name);
    }

    public Object get() {
        return data.get("default");
    }

    @Override
    public String toString() {
        return null;
    }
}
