package platform.config;

import com.alibaba.fastjson.JSONObject;

public class ActorConfig {
    private String ActorName;

    public ActorConfig(JSONObject object) {
        this.ActorName = object.getString("ActorName");
    }

    public String getActorName() {
        return ActorName;
    }

    @Override
    public String toString() {
        return "ActorConfig{" +
                "ActorName='" + ActorName + '\'' +
                '}';
    }
}
