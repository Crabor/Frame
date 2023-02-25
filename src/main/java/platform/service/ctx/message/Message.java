package platform.service.ctx.message;

import platform.service.ctx.ctxChecker.context.Context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Message {
    private final long index;
    private Context context;

    public Message(long index) {
        this.index = index;
    }

    public void addContext(Context context){ this.context = context;}


    public long getIndex() {
        return index;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "Message{" +
                "index=" + index +
                ", context=" + context +
                '}';
    }
}
