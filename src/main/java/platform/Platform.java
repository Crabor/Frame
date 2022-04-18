package platform;

import platform.app.AppMgrThread;
import platform.pubsub.Channel;
import platform.resource.ResMgrThread;
import platform.pubsub.Publisher;
import platform.pubsub.AbstractSubscriber;
import platform.service.SerMgrThread;
import io.lettuce.core.RedisClient;

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
        appMgr.start();
        serMgr.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Channel c : Channel.getObjs()) {
            System.out.println(c);
        }
        System.out.println(AbstractSubscriber.getObjs());

        while(true);
    }

    public static void Close() {
        Publisher.Close();
        AbstractSubscriber.Close();
    }
}
