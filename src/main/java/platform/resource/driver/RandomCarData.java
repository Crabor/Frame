package platform.resource.driver;

import java.util.Random;

public class RandomCarData {
    public static String randomJSONCarData(){
        return "{\"front\":" + Math.random()*30 +
                ", \"back\":" + Math.random()*30 +
                ", \"left\":" + Math.random()*30 +
                ", \"right\":" + Math.random()*30 +"}";
    }
    public static void main(String [] args){

        System.out.println(randomJSONCarData());
    }
}
