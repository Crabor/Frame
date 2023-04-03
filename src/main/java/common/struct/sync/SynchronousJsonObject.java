package common.struct.sync;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousJsonObject {
    private LinkedBlockingQueue<JSONObject> queue;

    public SynchronousJsonObject() {
        queue = new LinkedBlockingQueue<>();
    }

    public SynchronousJsonObject(int capacity) {
        queue = new LinkedBlockingQueue<>(capacity);
    }

    public JSONObject blockTake() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject blockTake(long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            return null;
        }
    }

    public JSONObject nonBlockTake() {
        return queue.poll();
    }

    public void put(JSONObject data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}
