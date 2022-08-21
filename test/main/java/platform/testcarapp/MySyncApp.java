package platform.testcarapp;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.app.AbstractSyncApp;
import platform.service.inv.CancerArray;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.struct.Actor;

public class MySyncApp extends AbstractSyncApp {
    private static final Log logger = LogFactory.getLog(MySyncApp.class);

    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        Actor actor = new Actor(2, 0, 0);

        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CheckInfo[] checkInfos = ca.check();
        for (int i = 0; i< checkInfos.length; i++) {
            if (checkInfos[i].name.equals("left")) {
                if (checkInfos[i].checkState == CheckState.INV_VIOLATED) {
                    actor.setYSpeed(-1 * checkInfos[i].diff);
                }
            }
        }
        publish("check", JSON.toJSONString(checkInfos));
        publish("actor", JSON.toJSONString(actor));
        logger.debug("actor: " + JSON.toJSONString(actor));
        logger.debug("check:\n" + JSON.toJSONString(checkInfos, true));
    }
}
