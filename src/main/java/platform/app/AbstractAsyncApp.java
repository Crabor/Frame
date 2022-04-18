package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerObject;

public abstract class AbstractAsyncApp extends AbstractSubscriber implements AsyncApp, Runnable {
    private Thread t;
    private int sleepTime;
    protected String appName;

    public AbstractAsyncApp() {
        this.appName = getClass().getName();
    }

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
            CancerObject.iterEntry(appName);
            iter();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, getClass().getName());
            t.start ();
        }
    }
}
