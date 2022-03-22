package platform.ui;

public class UIMgrThread implements Runnable{
    private static UIMgrThread instance;
    private static Thread t;

    // 构造方法私有化
    private UIMgrThread() {}

    // 静态方法返回该实例
    public static UIMgrThread getInstance() {
        // 第一次检查instance是否被实例化出来，如果没有进入if块
        if(instance == null) {
            synchronized (UIMgrThread.class) {
                // 某个线程取得了类锁，实例化对象前第二次检查instance是否已经被实例化出来，如果没有，才最终实例出对象
                if (instance == null) {
                    instance = new UIMgrThread();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        //TODO
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, "UIMgrThread");
            t.start ();
        }
    }
}
