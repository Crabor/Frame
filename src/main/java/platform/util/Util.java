package platform.util;

public class Util {
    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String randomJSONCarData(){
        return "{\"front\":" + Math.random()*30 +
                ", \"back\":" + Math.random()*30 +
                ", \"left\":" + Math.random()*30 +
                ", \"right\":" + Math.random()*30 +"}";
    }
}
