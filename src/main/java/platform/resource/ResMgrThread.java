package platform.resource;

import platform.pubsub.Channel;
import platform.resource.driver.DeviceDriver;
import platform.resource.driver.DBDriver;

public class ResMgrThread implements Runnable {
    private static ResMgrThread instance;
    private static Thread t;


    // 构造方法私有化
    private ResMgrThread() {
    }

    // 静态方法返回该实例
    public static ResMgrThread getInstance() {
        // 第一次检查instance是否被实例化出来，如果没有进入if块
        if (instance == null) {
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
        //init resource
        Channel sensor = new Channel("sensor");
        Channel actor = new Channel("actor");
        Channel check = new Channel("check");
        Channel ctxStat = new Channel("ctxStat");

        DeviceDriver dd = new DeviceDriver(8080, "127.0.0.1", 8081);
        dd.subscribe("actor", 0, 0);
        dd.start();

        DBDriver ud = new DBDriver(8082, "127.0.0.1", 8083);
        ud.subscribe("sensor", 0, 0);
        ud.subscribe("actor", 1, 0);
        ud.subscribe("check", 0, 0);
        ud.subscribe("ctxStat");
        ud.start();
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "ResMgrThread");
            t.start();
        }
    }


}
