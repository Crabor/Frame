package platform.config;

import com.alibaba.fastjson.JSONObject;
import platform.pubsub.Channel;

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
        try {
            this.groupId = object.getInteger("groupId");
        } catch (NullPointerException e) {
            this.groupId = 0;
        }
        try {
            this.priorityId = object.getInteger("priorityId");
        } catch (NullPointerException e) {
            this.priorityId = 0;
        }
    }

    @Override
    public String toString() {
        return "{" + channel + "," + groupId + "," + priorityId + "}";
    }
}
