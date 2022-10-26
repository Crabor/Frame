package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import platform.app.AbstractApp;
import platform.config.Configuration;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;


public class MyApp extends AbstractApp {

    @Override
    protected void customizeCtxServer() {
        config.registerSensor("left");
    }

    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        Actor actor = new Actor(5,0,0);

        //method 1
        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CancerObject left = ca.get("left");
        CheckInfo checkInfo = left.check();
        logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
        if (checkInfo.checkState == CheckState.INV_VIOLATED) {
            actor.setYSpeed(-checkInfo.diff);
        }

        publish("actor", JSON.toJSONString(actor));
        logger.debug("actor: " + JSON.toJSONString(actor));
    }
}
