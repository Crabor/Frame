package platform.testInv;

import app.InvCheck;
import com.alibaba.fastjson.JSONObject;

public class test {
    public static void check(Object... args) {
    }

    public static void monitor(Object... args) {
    }

    public static void main(String[] args) {
        InvCheck invCheck = InvCheck.getInstance();
        int a = 1;
        int b = 2;
        check(a, b);
        monitor(a, b);
        check(a, b);
    }
}
