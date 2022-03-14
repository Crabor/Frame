package frame;

import frame.app.AppMgrThread;
import frame.resource.ResMgrThread;
import frame.pubsub.Publisher;
import frame.pubsub.Subscriber;
import frame.service.SerMgrThread;
import frame.ui.UIMgrThread;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

public class Frame {
    private static ResMgrThread resMgr;
    private static SerMgrThread serMgr;
    private static AppMgrThread appMgr;
    private static UIMgrThread uiMgr;

    public static void Init() throws IOException {
        //init mgr
        resMgr = ResMgrThread.getInstance();
        serMgr = SerMgrThread.getInstance();
        appMgr = AppMgrThread.getInstance();
        uiMgr = UIMgrThread.getInstance();

        //init database
        JedisPool pool = new JedisPool("127.0.0.1", 6379);
        Publisher.Init(pool);
        Subscriber.Init(pool);
    }

    public static void Start() {
        resMgr.start();
        serMgr.start();
        appMgr.start();
        uiMgr.start();
    }
}
