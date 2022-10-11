package platform.testunitycar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.app.AbstractSyncApp;
import platform.service.ctx.ctxServer.CtxInteractor;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;

public class MySyncApp extends AbstractSyncApp {

    public MySyncApp(){
        ctxInteractor.registerSensor("taxis");
    }

    @Override
    public void iter(String channel, String msg) {
        System.out.println("app recv " + msg);
        JSONObject msgJsonObg = JSONObject.parseObject(msg);
        if(msgJsonObg.get("index").equals("2")){
            ctxInteractor.cancelSensor("taxis");
        }
//        logger.debug("app recv: " + msg);
//        Actor actor = new Actor(5, 0, 0);
//
//        //method 1
//        CancerArray ca = CancerArray.fromJsonObjectString(msg);
//        CancerObject left = ca.get("left");
//        //method 2
//        //CancerObject left = CancerObject.fromJsonObjectString(ctxInteractor.getSensor("left"));//{"left": 10}
//        CheckInfo checkInfo = left.check();
//        logger.debug("check:\n" + JSON.toJSONString(checkInfo, true));
//        if (checkInfo.checkState == CheckState.INV_VIOLATED) {
//            actor.setYSpeed(-checkInfo.diff);
//        }
//
//        publish("actor", JSON.toJSONString(actor));
//        logger.debug("actor: " + JSON.toJSONString(actor));
    }
}
