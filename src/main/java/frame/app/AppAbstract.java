package frame.app;

public abstract class AppAbstract implements App {
    protected AppSubThread appSubThread;

    @Override
    public void init(App app) {
        appSubThread = new AppSubThread(app);

    }
}
