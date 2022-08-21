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
        this.appName = getClass().getName();
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
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
