package platform.resource;

import platform.Platform;
import platform.pubsub.Channel;
import platform.resource.driver.DeviceDriver;
import platform.resource.driver.DBDriver;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class ResMgrThread implements Runnable {
    private static ResMgrThread instance;
    private static Thread t;

    private DBDriver dbd;
    
    private DeviceDriver dd;


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
        dd = new DeviceDriver(8080, "127.0.0.1", 8081);
        dd.subscribe("actor", 0, 0);
        dd.start();

        dbd = new DBDriver();
        dbd.subscribe("sensor", 0, 0);
        dbd.subscribe("actor", 1, 0);
        dbd.subscribe("check", 0, 0);
        dbd.subscribe("ctxStat", 0, 0);
        dbd.start();

        Platform.incrMgrStartFlag();
    }

    public DBDriver getDBDriver() {
        return dbd;
    }

    public DeviceDriver getDeviceDriver() {
        return dd;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "ResMgrThread");
            t.start();
        }
    }


}
