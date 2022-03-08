package frame;

import java.io.IOException;

public class TestFrame {
    public static void main(String[] args) throws IOException {
        FrameConfig config = FrameConfig.readConfig("");
        config.app = new MyApp();
        Frame.Init(config);
        Frame.Start();
    }
}
