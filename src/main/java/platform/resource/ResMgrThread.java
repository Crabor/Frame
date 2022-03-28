package platform.resource;

import platform.pubsub.Channel;
import platform.resource.device.DeviceRTThread;
import platform.resource.device.DeviceSubscriber;

public class ResMgrThread implements Runnable{
    private static ResMgrThread instance;
    private static Thread t;


    // 构造方法私有化
    private ResMgrThread() {}

    // 静态方法返回该实例
    public static ResMgrThread getInstance() {
        // 第一次检查instance是否被实例化出来，如果没有进入if块
        if(instance == null) {
            synchronized (ResMgrThread.class) {
                // 某个线程取得了类锁，实例化对象前第二次检查instance是否已经被实例化出来，如果没有，才最终实例出对象
                if (instance == null) {
                    instance = new ResMgrThread();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        //TODO
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, "ResMgrThread");
            t.start ();
        }
    }


}
