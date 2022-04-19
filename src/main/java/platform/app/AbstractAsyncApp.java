package platform.app;

import platform.pubsub.AbstractSubscriber;
import platform.service.inv.CancerObject;
import platform.service.inv.CancerServer;

public abstract class AbstractAsyncApp extends AbstractSubscriber implements AsyncApp, Runnable {
    private Thread t;
    private int sleepTime;
    protected String appName;
    protected int iterId;

    public AbstractAsyncApp() {
        this.appName = getClass().getSimpleName();
        iterId = 0;
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
            iterId++;
            CancerServer.iterEntry(appName, iterId);
            iter();
            CancerServer.iterExit(appName, iterId);
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, getClass().getSimpleName());
            t.start ();
        }
    }
}
