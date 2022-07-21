package platform;

import platform.app.AppMgrThread;
import platform.pubsub.Channel;
import platform.resource.ResMgrThread;
import platform.pubsub.Publisher;
import platform.pubsub.AbstractSubscriber;
import platform.service.SerMgrThread;
import io.lettuce.core.RedisClient;
import platform.service.cxt.CxtSubscriber;
import platform.service.inv.CancerServer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Platform {
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

    public static void Init() {
        //init database
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);

        //init mgr
        resMgr = ResMgrThread.getInstance();
        appMgr = AppMgrThread.getInstance();
        serMgr = SerMgrThread.getInstance();
    }

    public static void Start() {
        resMgr.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serMgr.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appMgr.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            mgrStartFlagLock.lock();
            if (mgrStartFlag == 3) {
                mgrStartFlagLock.unlock();
                break;
            }
            mgrStartFlagLock.unlock();
        }
        System.out.println("channels:");
        for (Channel c : Channel.getObjs()) {
            System.out.println(c);
        }
        System.out.println("subscribers: " + AbstractSubscriber.getObjs());

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
