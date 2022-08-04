package platform.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDriverConfig {
    public List<SubConfig> subConfigs = new ArrayList<>();

    public DatabaseDriverConfig(JSONObject object) {
        JSONArray subs = object.getJSONArray("subscribe");
        for (int i = 0; i < subs.size(); i++) {
            JSONObject sub = subs.getJSONObject(i);
            subConfigs.add(new SubConfig(sub));
        }
    }

    public List<SubConfig> getSubConfigs() {
        return subConfigs;
    }

    @Override
    public String toString() {
        return "DatabaseDriverConfig{" +
                "subConfigs=" + subConfigs +
                '}';
    }
}
