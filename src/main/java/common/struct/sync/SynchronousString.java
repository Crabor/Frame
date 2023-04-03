package common.struct.sync;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousString {
    private LinkedBlockingQueue<String> queue;

    public SynchronousString() {
        queue = new LinkedBlockingQueue<>();
    }

    public SynchronousString(int capacity) {
        queue = new LinkedBlockingQueue<>(capacity);
    }

    public String blockTake() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String blockTake(long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            return null;
        }
    }

    public String nonBlockTake() {
        return queue.poll();
    }

    public void put(String data) {
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
