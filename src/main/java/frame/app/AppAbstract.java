package frame.app;

public abstract class AppAbstract implements App {
    protected AppSubThread appSubThread;

    @Override
    public void init() {
        appSubThread = new AppSubThread(this);

    }
}
