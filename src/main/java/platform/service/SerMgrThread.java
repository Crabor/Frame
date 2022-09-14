package platform.service;

import platform.Platform;
import platform.config.Configuration;
import platform.service.ctx.CtxSubscriber;
import platform.service.inv.CancerServer;
import platform.config.SubConfig;

public class SerMgrThread implements Runnable{
    private static SerMgrThread instance;
    private static Thread t;

    private CtxSubscriber ctxSubscriber;

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
        if (Configuration.getCtxServerConfig().isServerOn()) {
            ctxSubscriber = CtxSubscriber.getInstance();
            for (SubConfig subConfig : Configuration.getCtxServerConfig().getSubConfigs()) {
                ctxSubscriber.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId);
            }
            ctxSubscriber.start();
        }

        if (Configuration.getInvServerConfig().isServerOn()) {
            cancerServer = CancerServer.getInstance();
            for (SubConfig subConfig : Configuration.getInvServerConfig().getSubConfigs()) {
                cancerServer.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId);
            }
            cancerServer.start();
        }

        Platform.incrMgrStartFlag();
    }

    public CtxSubscriber getCxtSubscriber() {
        return ctxSubscriber;
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
