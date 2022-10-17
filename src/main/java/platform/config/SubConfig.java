package platform.config;

import com.alibaba.fastjson.JSONObject;
import platform.pubsub.Channel;
import platform.pubsub.Subscribe;
import platform.struct.GrpPrioMode;

public class SubConfig {
    public String channel;
    public int groupId;
    public int priorityId;
    public long mode;

    public SubConfig(String channel, int groupId, int priorityId, long mode) {
        this.channel = channel;
        this.groupId = groupId;
        this.priorityId = priorityId;
        this.mode = mode;
    }

    public SubConfig(String channel, GrpPrioMode gpm) {
        this(channel, gpm.groupId, gpm.priorityId, gpm.mode);
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
        try {
            this.mode = object.getLong("mode");
        } catch (NullPointerException e) {
            this.mode = 0;
        }
    }

    @Override
    public String toString() {
        return "{" + channel + "," + groupId + "," + priorityId +  "," + mode + "}";
    }
}
