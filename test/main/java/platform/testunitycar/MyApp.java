package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import platform.app.AbstractApp;
import platform.comm.socket.UDP;
import platform.config.Configuration;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.util.Util;


public class MyApp extends AbstractApp {

    @Override
    protected void customizeCtxServer() {
        config.registerSensor("left");
    }

    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        Actuator actuator = new Actuator(5,0,0);

        //method 1
        CancerObject left = CancerObject.fromJsonObjectString(msg);
        CheckInfo checkInfo = left.check();
        logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
        if (checkInfo.checkState == CheckState.INV_VIOLATED) {
            actuator.setYSpeed(-checkInfo.diff);
        }

        UDP.send(Util.formatCommand("actuator_set", actuator.toString()));
        logger.debug("actuator: " + actuator);
    }
}
