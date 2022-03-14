package frame;

import frame.app.AppMgrThread;
import frame.resource.ResMgrThread;
import frame.service.SerMgrThread;
import frame.ui.UIMgrThread;

import java.io.IOException;

public class Frame {
    private static ResMgrThread resMgr;
    private static SerMgrThread serMgr;
    private static AppMgrThread appMgr;
    private static UIMgrThread uiMgr;

    public static void Init() throws IOException {
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
}
