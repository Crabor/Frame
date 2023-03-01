package common.struct;

import java.util.concurrent.LinkedBlockingQueue;

public class SynchronousSetState {
    private LinkedBlockingQueue<SetState> queue;

    public SynchronousSetState() {
        queue = new LinkedBlockingQueue<>();
    }

    public SynchronousSetState(int capacity) {
        queue = new LinkedBlockingQueue<>(capacity);
    }

    public SetState blockTake() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SetState nonBlockTake() {
        return queue.poll();
    }

    public void put(SetState data) {
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
