package frame.app;

public class App {
    AppFunc func;

    public App(AppConfig config) {
        this.func = config.func;
        // TODO
    }

    void run() {
        func.run();
    }
}
