package platform.app;

import platform.pubsub.AbstractSubscriber;

public abstract class AbstractAsyncApp extends AbstractSubscriber implements AsyncApp, Runnable {
    private Thread t;
    private int sleepTime;

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iter();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, getClass().getSimpleName());
            t.start ();
        }
    }
}
