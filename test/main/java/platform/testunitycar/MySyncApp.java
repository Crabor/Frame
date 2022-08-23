package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import platform.app.AbstractSyncApp;
import platform.service.inv.CancerArray;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;

public class MySyncApp extends AbstractSyncApp {
    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        Actor actor = new Actor(5, 0, 0);

        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CheckInfo[] checkInfos = ca.check();
        for (int i = 0; i< checkInfos.length; i++) {
            if (checkInfos[i].name.equals("left")) {
                if (checkInfos[i].checkState == CheckState.INV_VIOLATED) {
                    actor.setYSpeed(-1 * checkInfos[i].diff);
                }
            }
        }

        publish("actor", JSON.toJSONString(actor));
        logger.debug("actor: " + JSON.toJSONString(actor));
        logger.debug("check:\n" + JSON.toJSONString(checkInfos, true));
    }
}
