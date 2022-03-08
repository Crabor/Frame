package frame;

import frame.app.App;
import frame.resource.Resource;
import frame.service.Service;
import frame.ui.UI;

import java.io.IOException;
import java.util.List;

public class Frame {
    private static Resource resource;
    private static Service service;
    private static UI ui;
    private static App app;

    public static void Init(FrameConfig config) throws IOException {
        resource = new Resource(config.resourceConfig);
        service = new Service(config.serviceConfig);
        ui = new UI(config.uiConfig);
        app = config.app;
        app.init();
    }

    public static void Start() {
    }
}
