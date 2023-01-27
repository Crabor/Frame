package platform.app;

import platform.Platform;
import platform.config.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class AppMgrThread implements Runnable {
    private static AppMgrThread instance;
    private static Thread t;
    private final List<App> apps = new ArrayList<>();
    private static final Map<String, Set<Integer>> sensorDataChannelUDPPort = new HashMap<>();

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
//        for (AppConfig appConfig : Configuration.getAppsConfig().values()) {
//            try {
//                Object app = Class.forName(appConfig.getAppName()).newInstance();
//                apps.add((App) app);
//                appConfig.getSubConfigs().forEach(config -> {
//                    ((AbstractSubscriber) app).subscribe(config);
//                });
//                Platform.sysCall(appConfig.getAppName(), ServiceType.CTX, CmdType.START);
//                Platform.sysCall(appConfig.getAppName(), ServiceType.INV, CmdType.START);
//                ((AbstractApp) app).start();
//            } catch (InstantiationException |
//                    IllegalAccessException |
//                    ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        // TODO:
        Platform.incrMgrStartFlag();

        try {
            ServerSocket serverSocket = new ServerSocket(Configuration.getTcpConfig().getServerPort());
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new AppDriver(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "AppMgrThread");
            t.start();
        }
    }

    public List<App> getApps() {
        return apps;
    }

    public static int getNewSensorDataChannelUDPPort(Socket socket) {
        String clientHost = socket.getInetAddress().getHostAddress();
        int clientPort = socket.getPort();
        if (!sensorDataChannelUDPPort.containsKey(clientHost)) {
            sensorDataChannelUDPPort.put(clientHost, new HashSet<>());
        }
        Set<Integer> clientPorts = sensorDataChannelUDPPort.get(clientHost);
        clientPorts.add(clientPort);
        int i = 1;
        while (clientPorts.contains(clientPort + i)) {
            i++;
        }
        clientPorts.add(clientPort + i);
        return clientPort + i;
    }
}
