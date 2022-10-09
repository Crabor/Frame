package platform.service.ctx.message;

import platform.service.ctx.ctxChecker.context.Context;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private final long index;
    private final Map<String, Context> contextMap;

    public Message(long index) {
        this.index = index;
        this.contextMap = new HashMap<>();
    }


    public void addContext(Context context){
        contextMap.put(context.getContextId(), context);
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Message{" +
                "index=" + index +
                ", contextMap=" + contextMap +
                '}';
    }
}
