package platform.testother;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args)  {
        String expr = "a = 0; if (a == 0) { return 'hello';} else { return 'world';}";
        VariableResolverFactory resolverFactory = new MapVariableResolverFactory();
        System.out.println(MVEL.eval(expr, resolverFactory));
    }

}
