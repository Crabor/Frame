package platform.testother;

import common.util.Util;

import java.awt.*;

public class test {
    public static void main(String[] args) {
        //颜色名称转Color对象
        Color color = Util.parseColor("red");
        System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
    }
}
