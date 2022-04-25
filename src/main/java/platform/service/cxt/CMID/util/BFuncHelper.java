package platform.service.cxt.CMID.util;

import platform.service.cxt.CMID.context.Context;

/**
 * Created by njucjc on 2017/10/7.
 */
public class BFuncHelper {

    private static boolean isFly(Context c) {
        return c.getSensorName().equals("FlyObject");
    }

    private static boolean isShip(Context c) {
        return c.getSensorName().equals("ShipObject");
    }



    private static boolean isSameFly(Context c1, Context c2) {
        return c1.getNo() < c2.getNo() && isFly(c1) && isFly(c2) && c1.getId().equals(c2.getId());
    }

    private static boolean isSameShip(Context c1, Context c2) {
        return c1.getNo() < c2.getNo() && isShip(c1) && isShip(c2) && c1.getId().equals(c2.getId());
    }

    private static boolean flyControllable(Context c1, Context c2) {
        double sec = Math.abs(TimestampHelper.timestampDiff(c1.getTimestamp(), c2.getTimestamp())) / 1000.0;
        double dist = 0;
        return sec != 0 && dist / sec <= 600;
    }

    private static boolean shipControllable(Context c1, Context c2) {
        double sec = Math.abs(TimestampHelper.timestampDiff(c1.getTimestamp(), c2.getTimestamp())) / 1000.0;
        double dist = 0;
        return sec != 0 && dist / sec <= 20;
    }

    public static boolean bfun(String name, Context context1, Context context2) {
        boolean value = false;
        switch (name) {
            case "isFly":
                value = isFly(context1);
                break;
            case "isShip":
                value = isShip(context1);
                break;
            case "isSameFly":
                value = isSameFly(context1, context2);
                break;
            case "isSameShip":
                value = isSameShip(context1, context2);
                break;
            case "flyControllable":
                value = flyControllable(context1, context2);
                break;
            case "shipControllable":
                value = shipControllable(context1, context2);
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
