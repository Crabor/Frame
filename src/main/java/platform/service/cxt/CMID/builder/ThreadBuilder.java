package platform.service.cxt.CMID.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.cxt.CMID.checker.Checker;
import platform.config.PlatformConfig;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//TODO: waiting for concurrent controlling, and sending changes from platform the checkers
//TODO: Maybe "Consumer-Producer" Mode?

public class ThreadBuilder extends AbstractCheckerBuilder implements Runnable{
    private static final Log logger = LogFactory.getLog(ThreadBuilder.class);
    private DatagramSocket serverSocket;
    private boolean running;
    private byte [] buf = new byte[256];

    public ThreadBuilder(PlatformConfig platformConfig)  {
        super(platformConfig);
        if (!changeHandlerType.contains("dynamic")) {
            logger.info("配置文件配置错误：运行Server需将changeHandlerType配置为dynamic-change-based");
            System.exit(1);
        }
        logger.info("服务器开始启动");

        try {
            serverSocket = new DatagramSocket(port);
        }catch(IOException e) {
            e.printStackTrace();
        }
        logger.info("成功绑定" + port + "端口");
    }

    @Override
    public void run() {
        running = true;
        int count = 0;
        long timeSum = 0;
        Map<Long, List<String>> deleteChanges = new TreeMap<>();

        System.out.println("等待处理change......");

        while (running) {

            synchronized (PlatformConfig.changeListForChecking) {

                long start = System.nanoTime();

                for (String chg : PlatformConfig.changeListForChecking) {
                    changeHandler.doContextChange(chg);
                }

                long end = System.nanoTime();
                timeSum += (end - start);

                count++;

                int inc = 0;
                for (Checker checker : checkerList) {
                    inc += checker.getInc();
                }

                System.out.print("[INFO] INC: " + inc + "\tTotal Checking time: " + (timeSum / 1000000) + " ms\r");
                try {
                    PlatformConfig.changeListForChecking.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        changeHandler.shutdown();
        int inc = 0;
        for (Checker checker : checkerList) {
            inc += checker.getInc();
        }
        System.out.println();
        serverSocket.close();
        shutdown();
    }

    public static void main(String[] args) {
        if(args.length == 1) {
            Thread serverThread = new Thread(new ThreadBuilder(PlatformConfig.getInstace()));
            serverThread.setPriority(Thread.MAX_PRIORITY);
            serverThread.start();
        }
        else {
            System.out.println("Usage: java Server [configFilePath].");
        }
    }
}
