package frame;

import frame.app.App;
import frame.app.AppMgrThread;
import frame.resource.ResMgrThread;
import frame.resource.Resource;
import frame.service.SerMgrThread;
import frame.service.Service;
import frame.ui.UI;
import frame.ui.UIMgrThread;

import java.io.IOException;
import java.util.List;

public class Frame {
    private static ResMgrThread resMgr;
    private static SerMgrThread serMgr;
    private static AppMgrThread appMgr;
    private static UIMgrThread uiMgr;

    public static void Init(FrameConfig config) throws IOException {
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
