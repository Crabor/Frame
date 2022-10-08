package platform.service.ctx.message;

import platform.service.ctx.ctxChecker.context.Context;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private final long index;
    private final long timestamp;
    private final Map<String, Context> contextMap;

    public Message(long index, long timestamp) {
        this.index = index;
        this.timestamp = timestamp;
        this.contextMap = new HashMap<>();
    }

    public void addContext(Context context){
        contextMap.put(context.getContextId(), context);
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
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
                ", contextMap=" + contextMap +
                '}';
    }
}
