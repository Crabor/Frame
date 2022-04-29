package platform.service.cxt.CMID.util;

import platform.service.cxt.CMID.context.Context;

/**
 * Created by njucjc on 2017/10/7.
 */
public class BFuncHelper {

    private static boolean isValid(Context c) {
        if(Double.parseDouble(String.valueOf(c.getSensorData()))<30)
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
                System.out.println("[INFO] Illegal bfunc: " + name);
                System.exit(1);
                break;
        }
        return value;
    }

    public static void main(String[] args) {

    }
}
