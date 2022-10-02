package platform;

import platform.config.Configuration;
import platform.app.AppMgrThread;
import platform.pubsub.Channel;
import platform.resource.ResMgrThread;
import platform.pubsub.Publisher;
import platform.pubsub.AbstractSubscriber;
import io.lettuce.core.RedisClient;
import platform.util.Util;
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

    public static void Init() {
        //delete output dir
        File dir = new File("output/");
        Util.deleteDir(dir);
        dir.mkdirs();

        //read config file
        Configuration.analyzer("Resources/Configuration");

        //init database
        RedisClient client = RedisClient.create(
                "redis://" + Configuration.getRedisConfig().getServerAddress() + ":" +
                        Configuration.getRedisConfig().getServerPort());
        Publisher.Init(client);
        AbstractSubscriber.Init(client);

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

        logger.info("channels: ");
        for (Channel channel : Channel.getObjs()) {
            logger.info(channel);
        }
        logger.info("subscribers: " + AbstractSubscriber.getObjs());

//        for (Channel c : Channel.getObjs()) {
//            System.out.println(c);
//        }
//        System.out.println(AbstractSubscriber.getObjs());
//        System.out.println(CancerServer.getCheckMap());
//        System.out.println(CancerServer.getSegMap());
//        System.out.println();
    }

    public static void Close() {
        Publisher.Close();
        AbstractSubscriber.Close();
    }
}
