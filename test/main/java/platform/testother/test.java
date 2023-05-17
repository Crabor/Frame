package platform.testother;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.mvel2.MVEL;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args)  {
        //jep计算表达式
        String expression = "('App' == 'App' || 'App' == 'Resource' || 'App' == 'Service') ? 1 : 'App'";
        try {
            expression = String.valueOf(MVEL.eval(expression));
        } catch (Exception ignored) {}
        System.out.println(expression);
    }

}
