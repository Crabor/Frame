package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import platform.app.AbstractApp;
import platform.comm.socket.PlatformUDP;
import platform.struct.Cmd;
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

        //method 1
//        CancerObject left = CancerObject.fromJsonObjectString(msg);
//        CheckInfo checkInfo = left.check();
//        logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
//        if (checkInfo.checkState == CheckState.INV_VIOLATED) {
//            Cmd ySpeed = new Cmd("actuator_set", "ySpeed", String.valueOf(-checkInfo.diff));
//            logger.debug(ySpeed);
//            PlatformUDP.send(ySpeed);
//        }

        Cmd xSpeed = new Cmd("actuator_set", "xSpeed", "5");
        PlatformUDP.send(xSpeed);
        logger.debug(xSpeed);
    }
}
