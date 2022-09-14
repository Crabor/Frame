package platform.service.ctx.CMID.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.service.ctx.CMID.context.Context;

/**
 * Created by njucjc on 2017/10/7.
 */
public class BFuncHelper {
    private static final Log logger = LogFactory.getLog(BFuncHelper.class);
    private static boolean isValid(Context c) {
        double value = Double.parseDouble(String.valueOf(c.getSensorData()));
        if(value < 500 && value > -500)
            return true;
        else return false;
    }

    public static boolean bfun(String name, Context context1, Context context2) {
        boolean value = false;
        switch (name) {
            case "isValid":
                value = isValid(context1);
                break;
            default:
                logger.info("Illegal bfunc: " + name);
                System.exit(1);
                break;
        }
        return value;
    }

    public static void main(String[] args) {

    }
}
