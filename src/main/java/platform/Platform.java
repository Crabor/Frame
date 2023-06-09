package platform;

import common.struct.CtxServiceConfig;
import common.struct.InvServiceConfig;
import common.struct.ServiceConfig;
import org.apache.log4j.PropertyConfigurator;
import platform.config.Configuration;
import platform.app.AppMgrThread;
import platform.resource.ResMgrThread;
import platform.service.ctx.ctxServer.PlatformCtxServer;
import platform.service.inv.PlatformInvServer;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.ServiceType;
import common.util.Util;
import platform.service.SerMgrThread;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import  org.apache.commons.logging.Log;
import  org.apache.commons.logging.LogFactory;

public class Platform {
    private static final Log logger = LogFactory.getLog(Platform.class);
    private static ResMgrThread resMgr;
    private static SerMgrThread serMgr;
    private static AppMgrThread appMgr;
    private static final Lock mgrStartFlagLock = new ReentrantLock();
    private static int mgrStartFlag = 0;
    private static String runningId;

    static {
        PropertyConfigurator.configure("Resources/config/log/log4j.properties");
        File file = new File("output/log/platform/");
        Util.deleteDir(file);
    }

    public static void incrMgrStartFlag() {
        mgrStartFlagLock.lock();
        mgrStartFlag++;
        mgrStartFlagLock.unlock();
    }

    public static void lockUntilMgrStartFlagEqual(int value) {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mgrStartFlagLock.lock();
            if (mgrStartFlag == value) {
                mgrStartFlagLock.unlock();
                break;
            }
            mgrStartFlagLock.unlock();
        }
    }

    public static void Init(String configFile) {
        //delete output dir
        File dir = new File("output/");
        Util.deleteDir(dir);
        dir.mkdirs();

        //read config file
        Configuration.analyzer(configFile);

        //init database
//        RedisClient client = RedisClient.create(
//                "redis://" + Configuration.getRedisConfig().getServerAddress() + ":" +
//                        Configuration.getRedisConfig().getServerPort());
//        Publisher.Init(client);
//        AbstractSubscriber.Init(client);

        //init mgr
        resMgr = ResMgrThread.getInstance();
        appMgr = AppMgrThread.getInstance();
        serMgr = SerMgrThread.getInstance();
    }

    public static void Start() {
        serMgr.start();
        lockUntilMgrStartFlagEqual(1);
        logger.info("SerMgrThread started");
        resMgr.start();
        lockUntilMgrStartFlagEqual(2);
        logger.info("ResMgrThread started");
        appMgr.start();
        lockUntilMgrStartFlagEqual(3);
        logger.info("AppMgrThread started");

//        logger.info("channels: ");
//        for (Channel channel : Channel.getObjs()) {
//            logger.info(channel);
//        }
//        logger.info("subscribers: " + AbstractSubscriber.getObjs());
    }

    public static void Close() {
//        Publisher.Close();
//        AbstractSubscriber.Close();
    }

    public static boolean call(String appName, ServiceType type, CmdType cmd, ServiceConfig config) {
        boolean ret = false;
        switch (type) {
            case CTX:
                ret = PlatformCtxServer.call(appName, cmd, (CtxServiceConfig) config);
                break;
            case INV:
                ret = PlatformInvServer.call(appName, cmd, (InvServiceConfig) config);
                break;
            case ALL:
                ret = PlatformCtxServer.call(appName, cmd, (CtxServiceConfig) config);
                ret = PlatformInvServer.call(appName, cmd, (InvServiceConfig) config) && ret;
                break;
        }
        return ret;
    }

    public static void main(String[] args) {
        Init("Resources/config/platform/ConfigurationNew");
        Start();
    }
}
