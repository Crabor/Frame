package platform.testother;

import common.struct.sync.SynchronousString;

public class testsync {
    public static void main(String[] args) throws InterruptedException {
        SynchronousString ss = new SynchronousString();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String s = ss.blockTake();
                System.out.println("take: " + s);
            }
        }).start();
        Thread.sleep(3000);
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                ss.put("hello" + i);
                System.out.println("put: hello" + i);
            }
        }).start();
    }
}
