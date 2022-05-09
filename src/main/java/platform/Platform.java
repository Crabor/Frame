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

public class Platform {
    private static ResMgrThread resMgr;
    private static SerMgrThread serMgr;
    private static AppMgrThread appMgr;

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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serMgr.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appMgr.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("channels:");
        for (Channel c : Channel.getObjs()) {
            System.out.println(c);
        }
        System.out.println("subscribers:" + AbstractSubscriber.getObjs());


        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println((100 * CxtSubscriber.fixCount / CxtSubscriber.onMessageCount) + "%");
//            for (Channel c : Channel.getObjs()) {
//                System.out.println(c);
//            }
//            System.out.println(AbstractSubscriber.getObjs());
//            System.out.println(CancerServer.getCheckMap());
//            System.out.println(CancerServer.getSegMap());
//            System.out.println();
        }
    }

    public static void Close() {
        Publisher.Close();
        AbstractSubscriber.Close();
    }
}
