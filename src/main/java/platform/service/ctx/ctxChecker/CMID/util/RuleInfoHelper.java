package platform.service.ctx.ctxChecker.CMID.util;

import platform.service.ctx.ctxChecker.CMID.context.Context;
import platform.service.ctx.ctxChecker.CMID.context.ContextParser;

public class RuleInfoHelper {
    public static String translate(String ruleName, String link) {
        String [] jsonStrArr = link.split(" ");
        Context c1 = null, c2 = null;
        if (jsonStrArr.length == 1) {
            c1 = ContextParser.jsonToContextWithNo(jsonStrArr[0]);
        }
        else if (jsonStrArr.length == 2){
            c1 = ContextParser.jsonToContextWithNo(jsonStrArr[0]);
            c2 = ContextParser.jsonToContextWithNo(jsonStrArr[1]);
        }

        return "";
    }
}
