package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import platform.app.AbstractSyncApp;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.util.Util;

public class MySyncApp extends AbstractSyncApp {
    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        Actor actor = new Actor(0.15,0,0);

        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CancerObject left = ca.get("left");
        if (left != null) {
            CheckInfo checkInfo = left.check();
            logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
            if (checkInfo.checkState == CheckState.INV_VIOLATED) {
                actor.setYSpeed(Util.limit(-0.4, 0.4, -0.01 * checkInfo.diff));
            }
        }
        CancerObject front = ca.get("front");
        if (front.getValue() < 15) {
            actor.setXSpeed(0);
            actor.setYSpeed(0);
        }

        publish("actor", JSON.toJSONString(actor));
        logger.debug("actor: " + JSON.toJSONString(actor));
    }
}
