package frame;

import frame.app.AppMgrThread;
import frame.resource.ResMgrThread;
import frame.pubsub.Publisher;
import frame.pubsub.AbstractSubscriber;
import frame.service.SerMgrThread;
import frame.ui.UIMgrThread;
import io.lettuce.core.RedisClient;

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
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);
    }

    public static void Start() {
        resMgr.start();
        serMgr.start();
        appMgr.start();
        uiMgr.start();
    }

    public static void Close() {
        Publisher.Close();
        AbstractSubscriber.Close();
    }
}
