package frame;

import frame.app.App;
import frame.resource.Resource;
import frame.resource.ResourceConfig;
import frame.service.Service;
import frame.service.ServiceConfig;
import frame.ui.UI;
import frame.ui.UIConfig;

public class FrameConfig {
    public ResourceConfig resourceConfig;
    public ServiceConfig serviceConfig;
    public UIConfig uiConfig;
    public App app;

    public static FrameConfig readConfig(String filePath) {
        return null;
    }
}
