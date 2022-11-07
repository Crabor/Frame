package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import platform.app.AbstractApp;
import platform.comm.socket.Cmd;
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
        CancerObject left = CancerObject.fromJsonObjectString(msg);
        CheckInfo checkInfo = left.check();
        logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
        if (checkInfo.checkState == CheckState.INV_VIOLATED) {
            actor.setYSpeed(-checkInfo.diff);
        }

        Cmd.send("actuator_set", actor.toString());
        logger.debug("actor: " + actor);
    }
}
