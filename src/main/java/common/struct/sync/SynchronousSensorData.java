package common.struct.sync;

import common.struct.SensorData;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    public SensorData blockTake(long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
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

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}
