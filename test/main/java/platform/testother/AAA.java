package platform.testother;

public abstract class AAA implements Runnable {
    Thread t;
    @Override
    public void run(){
        System.out.println("AAA");
    }
    public void start() {
        if (t == null) {
            t = new Thread(this, getClass().getName());
            t.start();
        }
    }
}
