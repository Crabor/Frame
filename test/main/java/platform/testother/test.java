package platform.testother;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import tk.pratanumandal.expr4j.ExpressionEvaluator;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        String str = "1 + 2 * 3 ";
        //计算结果
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        System.out.println((int)evaluator.evaluate(str));
        String str1 = "1 + 2 * 5 ";
        //计算结果
        System.out.println((int)evaluator.evaluate(str1));
    }

}
