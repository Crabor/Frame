package platform.service;

import platform.pubsub.AbstractSubscriber;
import platform.pubsub.Channel;
import platform.service.cxt.CMID.builder.CheckerBuilder;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.CxtSubscriber;
import platform.service.inv.CancerServer;

import static platform.service.cxt.Interactor.ctxServiceStart;
import static platform.service.cxt.Interactor.sensorRegistAll;

public class SerMgrThread implements Runnable{
    private static SerMgrThread instance;
    private static Thread t;

    // 构造方法私有化
    private SerMgrThread() {}

    // 静态方法返回该实例
    public static SerMgrThread getInstance() {
        // 第一次检查instance是否被实例化出来，如果没有进入if块
        if (instance == null) {
            synchronized (SerMgrThread.class) {
                // 某个线程取得了类锁，实例化对象前第二次检查instance是否已经被实例化出来，如果没有，才最终实例出对象
                if (instance == null) {
                    instance = new SerMgrThread();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        //init cxt & inv
        // wang hui yan
        ctxServiceStart();
        sensorRegistAll();
        CxtSubscriber cxtSubscriber = new CxtSubscriber();
        cxtSubscriber.subscribe("sensor", 1, 1);
        Thread checkerThread = new Thread(new CheckerBuilder(PlatformConfig.getInstace()));
        checkerThread.setPriority(Thread.MAX_PRIORITY);
        checkerThread.start();

        CancerServer cancerServer = CancerServer.getInstance();
        cancerServer.subscribe("check", 1, 0);
        cancerServer.subscribe("sensor", 1, 0);
        cancerServer.start();
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "SerMgrThread");
            t.start();
        }
    }
}
