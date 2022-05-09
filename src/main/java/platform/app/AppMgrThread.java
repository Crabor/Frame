package platform.app;

import platform.app.userapps.MySyncApp;
import platform.pubsub.AbstractSubscriber;
import platform.service.cxt.Config.AppConfig;
import platform.service.cxt.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppMgrThread implements Runnable {
    private static AppMgrThread instance;
    private static Thread t;
    private final List<String> appNames = new ArrayList<>();

    // 构造方法私有化
    private AppMgrThread() {
    }

    // 静态方法返回该实例
    public static AppMgrThread getInstance() {
        // 第一次检查instance是否被实例化出来，如果没有进入if块
        if (instance == null) {
            synchronized (AppMgrThread.class) {
                // 某个线程取得了类锁，实例化对象前第二次检查instance是否已经被实例化出来，如果没有，才最终实例出对象
                if (instance == null) {
                    instance = new AppMgrThread();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        //init app
        for (AppConfig appConfig : Configuration.getListOfAppObj()) {
            appNames.add(appConfig.getAppName());
            try {
                Object app = Class.forName(appConfig.getAppName()).newInstance();
                ((App) app).setSleepTime(appConfig.getSleepTime());
                ((AbstractSubscriber) app).subscribe("sensor", 1, 0);
            } catch (InstantiationException |
                    IllegalAccessException |
                    ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "AppMgrThread");
            t.start();
        }
    }

    public List<String> getAppNames() {
        return appNames;
    }
}
