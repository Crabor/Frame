package platform.service.ctx.Contexts;

import com.alibaba.fastjson.JSONObject;

public class Message {
    private final long index;
    private final long timestamp;
    private final JSONObject msgObj;

    public Message(long index, long timestamp, JSONObject msgObj) {
        this.index = index;
        this.timestamp = timestamp;
        this.msgObj = msgObj;
    }

    public JSONObject getMsgObj() {
        return msgObj;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Message{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", msgObj=" + msgObj +
                '}';
    }
}
