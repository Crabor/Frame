package platform.app.userapps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.app.AbstractSyncApp;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.struct.Actor;

public class MySyncApp extends AbstractSyncApp {
    double ratio = 1;
    @Override
    public void iter(String channel, String msg) {
        System.out.println("sensor:" + msg);
        Actor actor = new Actor(2, 0, 0);

        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CheckInfo[] checkInfos = ca.check();
        publish("check", JSON.toJSONString(checkInfos));
        for (CheckInfo checkInfo : checkInfos) {
            if (checkInfo.name.equals("left")) {
                if (checkInfo.checkState == CheckState.INV_VIOLATED) {
                    actor.setYSpeed(ratio);
                    ratio += 0.01;
                    System.out.println("left violated " + ratio);
                } else {
                    ratio = 1;
                    System.out.println("left not violated");
                }
            }
        }
        System.out.println("actor: " + JSON.toJSONString(actor));
        publish("actor", JSON.toJSONString(actor));
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
