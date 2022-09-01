package platform.resource;

import platform.Platform;
import platform.resource.driver.DeviceDriver;
import platform.resource.driver.DBDriver;
import platform.config.DatabaseDriverConfig;
import platform.config.DeviceDriverConfig;
import platform.config.SubConfig;
import platform.config.Configuration;

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
        DeviceDriverConfig ddc = Configuration.getResourceConfig().getDeviceDriverConfig();
        dd = new DeviceDriver(ddc.serverPort, ddc.clientAddress, ddc.clientPort);
        for (SubConfig subConfig : ddc.getSubConfigs()) {
            dd.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId);
        }

        DatabaseDriverConfig dbdc = Configuration.getResourceConfig().getDatabaseDriverConfig();
        dbd = new DBDriver();
        for (SubConfig subConfig : dbdc.getSubConfigs()) {
            dbd.subscribe(subConfig.channel, subConfig.groupId, subConfig.priorityId);
        }

        Platform.incrMgrStartFlag();

        Platform.lockUntilMgrStartFlagEqual(3);
        dd.start();
        dbd.start();
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
