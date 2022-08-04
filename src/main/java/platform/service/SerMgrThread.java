package platform.service;

import platform.Platform;
import platform.service.cxt.CMID.builder.CheckerBuilder;
import platform.config.PlatformConfig;
import platform.config.Configuration;
import platform.service.cxt.CxtSubscriber;
import platform.service.inv.CancerServer;
import platform.config.SubConfig;

import static platform.service.cxt.Interactor.*;

public class SerMgrThread implements Runnable{
    private static SerMgrThread instance;
    private static Thread t;

    private CxtSubscriber cxtSubscriber;

    private CancerServer cancerServer;

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
        ruleRegistAll();
        sensorRegistAll();
        Thread checkerThread = new Thread(new CheckerBuilder(PlatformConfig.getInstace()));
        checkerThread.setPriority(Thread.MAX_PRIORITY);
        checkerThread.start();
        if (Configuration.getPlatformConfig().isServerOn()) {
            cxtSubscriber = CxtSubscriber.getInstance();
            for (SubConfig subConfig : Configuration.getPlatformConfig().getSubConfigs()) {
                cxtSubscriber.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId);
            }
            cxtSubscriber.start();
        }

        if (Configuration.getCancerServerConfig().isServerOn()) {
            cancerServer = CancerServer.getInstance();
            for (SubConfig subConfig : Configuration.getCancerServerConfig().getSubConfigs()) {
                cancerServer.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId);
            }
            cancerServer.start();
        }

        Platform.incrMgrStartFlag();
    }

    public CxtSubscriber getCxtSubscriber() {
        return cxtSubscriber;
    }

    public CancerServer getCancerServer() {
        return cancerServer;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "SerMgrThread");
            t.start();
        }
    }
}
