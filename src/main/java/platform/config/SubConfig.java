package platform.config;

import com.alibaba.fastjson.JSONObject;

public class SubConfig {
    public String channel;
    public int groupId;
    public int priorityId;

    public SubConfig(String channel, int groupId, int priorityId) {
        this.channel = channel;
        this.groupId = groupId;
        this.priorityId = priorityId;
    }

    public SubConfig(JSONObject object) {
        this.channel = object.getString("channel");
        this.groupId = object.getInteger("groupId");
        this.priorityId = object.getInteger("priorityId");
    }

    @Override
    public String toString() {
        return "{" + channel + "," + groupId + "," + priorityId + "}";
    }
}
