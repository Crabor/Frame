package platform.util;

public class Util {
    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }


}
