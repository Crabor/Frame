package platform.service;

import platform.Platform;
import platform.service.ctx.ctxServer.PlatformCtxServer;
import platform.service.inv.PlatformInvServer;

public class SerMgrThread implements Runnable{
    private static SerMgrThread instance;
    private static Thread t;

    private static PlatformCtxServer platformCtxServer;

    private static PlatformInvServer platformInvServer;

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
        //init cxt & invDaikon
        //TODO:platformCtxServer初始化如何拦截所有grp?

//        if (Configuration.getInvServerConfig().isServerOn()) {
//            platformInvServer = PlatformInvServer.getInstance();
//            platformInvServer.start();
//        }

        Platform.incrMgrStartFlag();
    }

    public static PlatformCtxServer getPlatformCtxServer() {
        return platformCtxServer;
    }

    public static PlatformInvServer getCancerServer() {
        return platformInvServer;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "SerMgrThread");
            t.start();
        }
    }
}
