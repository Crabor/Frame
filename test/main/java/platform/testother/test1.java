package platform.testother;

public class test1 {
    public static class O {
        int a;
        public void set(int a) {
            this.a = a;
        }

        @Override
        public String toString() {
            return "O{" +
                    "a=" + a +
                    '}';
        }
    }

    static boolean func(int a, O b) {
        b.set(a);
        return true;
    }

    public static void main(String[] args) {
        O b = new O();
        func(1, b);
        System.out.println(b);
    }
}
