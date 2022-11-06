package platform.testother;

import platform.util.Util;

import java.util.HashSet;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        Set<String> s = new HashSet<>();
        s.add("1");
        s.add("2");
        s.add("3");

        System.out.println(Util.setToString(s, " "));
    }
}
