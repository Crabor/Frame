package app;

import app.struct.SensorInfo;
import common.struct.SensorData;

import java.util.Map;

public class AppDemo2 extends AbstractApp {
    @Override
    public void setting() {
    }

    @Override
    public void getMsg(String sensorName, SensorData value) {
        // 因为注册了front传感器，所以每当front有新的数据时便会触发getMsg，
        // 其中channel是sensor名字，msg是sensor数据
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
        //用户代码
    }

    public static void main(String[] args) {
//        try {
//            AppDemo2 app = new AppDemo2();
//            app.connect("127.0.0.1", 8888);
//            //启动五个子线程
//            for (int i = 0; i < 10; i++) {
//                new Thread(() -> {
//                    while (true) {
////                        try {
////                            Thread.sleep(50);
////                        } catch (InterruptedException e) {
////                            throw new RuntimeException(e);
////                        }
//                        app.getSupportedSensors();
//                    }
//                }).start();
//            }
//            while (true);
////            app.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
