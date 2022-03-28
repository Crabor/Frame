package platform;

import platform.app.AppMgrThread;
import platform.resource.ResMgrThread;
import platform.pubsub.Publisher;
import platform.pubsub.AbstractSubscriber;
import platform.service.SerMgrThread;
import platform.ui.UIMgrThread;
import io.lettuce.core.RedisClient;

import java.io.IOException;

public class Platform {
    private static ResMgrThread resMgr;
    private static SerMgrThread serMgr;
    private static AppMgrThread appMgr;
    private static UIMgrThread uiMgr;

    public static void Init() {
        //init database
        RedisClient client = RedisClient.create("redis://localhost:6379");
        Publisher.Init(client);
        AbstractSubscriber.Init(client);

        //init mgr
        resMgr = ResMgrThread.getInstance();
        serMgr = SerMgrThread.getInstance();
        appMgr = AppMgrThread.getInstance();
        uiMgr = UIMgrThread.getInstance();
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
