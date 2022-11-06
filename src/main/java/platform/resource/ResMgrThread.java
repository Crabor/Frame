package platform.resource;

import platform.Platform;
import platform.comm.socket.UDP;
import platform.config.*;
import platform.resource.driver.DeviceDriver;
import platform.resource.driver.DBDriver;
import platform.util.Util;

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
        dd = new DeviceDriver();
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
        //TODO
        String sensorNames = Util.setToString(Configuration.getResourceConfig().getSensorsConfig().keySet(), " ");
        String actuatorNames = Util.setToString(Configuration.getResourceConfig().getActuatorsConfig().keySet(), " ");
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000/ SensorConfig.getAliveFreq());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UDP.send(Util.formatCommand("sensor_alive", sensorNames));
            }
        }).start();

        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000/ SensorConfig.getValueFreq());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UDP.send(Util.formatCommand("sensor_get", sensorNames));
            }
        }).start();

        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000/ ActuatorConfig.getAliveFreq());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UDP.send(Util.formatCommand("actuator_alive", actuatorNames));
            }
        }).start();

        //zhangshuhui
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UDP.send(Util.formatCommand("sync", String.valueOf(System.currentTimeMillis())));
            }
        }).start();

        while (true);
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
