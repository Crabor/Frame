package common.struct;

import java.util.concurrent.LinkedBlockingQueue;

public class SynchronousSensorData {
    private LinkedBlockingQueue<SensorData> queue;

    public SynchronousSensorData() {
        queue = new LinkedBlockingQueue<>();
    }

    public SynchronousSensorData(int capacity) {
        queue = new LinkedBlockingQueue<>(capacity);
    }

    public SensorData blockTake() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SensorData nonBlockTake() {
        return queue.poll();
    }

    public void put(SensorData data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        queue.clear();
    }
}
