package platform.util;

public class Util {
    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String randomJSONCarData(){
        int max = 100;
        return "{\"front\":" + Math.random()*max +
                ", \"back\":" + Math.random()*max +
                ", \"left\":" + Math.random()*max +
                ", \"right\":" + Math.random()*max +"}";
    }
}
