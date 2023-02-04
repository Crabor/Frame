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
//    private final List<App> apps = new ArrayList<>();
    //第一维为ip，第二维为该ip已被占用的端口
    private static final Map<String, Set<Integer>> portMap = new HashMap<>();
    private static final Set<Integer> grpIdSet = new HashSet<>();

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

//    public List<App> getApps() {
//        return apps;
//    }

    public static int getNewPort(Socket socket) {
        String clientHost = socket.getInetAddress().getHostAddress();
        int clientPort = socket.getPort();
        if (!portMap.containsKey(clientHost)) {
            portMap.put(clientHost, new HashSet<>());
        }
        Set<Integer> clientPorts = portMap.get(clientHost);
        clientPorts.add(clientPort);
        int i = 1;
        while (clientPorts.contains(clientPort + i)) {
            i++;
        }
        clientPorts.add(clientPort + i);
        return clientPort + i;
    }

    public static void removePort(Socket socket, int udpPort) {
        String clientHost = socket.getInetAddress().getHostAddress();
        int clientPort = socket.getPort();
        Set<Integer> s = portMap.get(clientHost);
        s.remove(clientPort);
        s.remove(udpPort);
    }

    public static int getNewGrpId() {
        int ret = (int) (System.currentTimeMillis() % 65536);
        int i = 0;
        while (grpIdSet.contains(ret + i)) {
            i++;
        }
        return ret + i;
    }

    public static void removeGrpId(int grpId) {
        grpIdSet.remove(grpId);
    }
}
