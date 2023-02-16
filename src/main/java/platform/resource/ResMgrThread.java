package platform.resource;

import common.socket.UDP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.Platform;
import platform.communication.socket.PlatformUDP;
import platform.communication.socket.Cmd;
import platform.config.*;
import platform.resource.driver.DeviceDriver;
import platform.resource.driver.DBDriver;

import java.util.concurrent.locks.LockSupport;

public class ResMgrThread implements Runnable {
    private static ResMgrThread instance;
    private static Thread t;
    private final Log logger = LogFactory.getLog(ResMgrThread.class);

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
        Configuration.getResourceConfig().getSensorsConfig().forEach((name, config) -> {
            //alive thread
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000 / config.getAliveFreq());
                        Cmd sensor_alive = new Cmd("sensor_alive", name);
                        PlatformUDP.send(sensor_alive);
//                        logger.debug(sensor_alive);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //get value thread
//            Thread valueThread = new Thread(() -> {
//                while (true) {
//                    try {
//                        Thread.sleep(1000 / config.getValueFreq());
//                        if (config.isAlive()) {
//                            Cmd sensor_get = new Cmd("sensor_get", config.getSensorName());
//                            PlatformUDP.send(sensor_get);
////                            logger.debug(sensor_get);
//                        }
//                    } catch (ArithmeticException e) {
//                        //说明valueFreq == 0，即不是定时获取sensor value，而是由用户主动调用或者驱动程序主动push上来
//                        LockSupport.park();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            Thread valueThread = new Thread(new Runnable() {
//                private volatile boolean shouldStop = false;
//                @Override
//                public void run() {
//                    shouldStop = false;
//                    while (!shouldStop) {
//
//                    }
//                }
//
//                public void stopThread() {
//                    shouldStop = true;
//                }
//            });
//            ValueThread valueThread = new ValueThread(config);
//            config.setValueThread(valueThread);
//            valueThread.start();
        });

        Configuration.getResourceConfig().getActuatorsConfig().forEach((name, config) -> {
            //alive thread
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000 / config.getAliveFreq());
                        Cmd actuator_alive = new Cmd("actuator_alive", name);
                        PlatformUDP.send(actuator_alive);
//                        logger.debug(actuator_alive);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        Configuration.getResourceConfig().getSensorsConfig().forEach((name, config) -> {
//            config.setValueFreq(1);
//        });
//        Configuration.getResourceConfig().getActuatorsConfig().forEach((name, config) -> {
//            config.setAliveFreq(2);
//        });

//        while (true);
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
