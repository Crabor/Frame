package platform.app.userapps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.app.AbstractSyncApp;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.struct.Actor;

public class MySyncApp extends AbstractSyncApp {
    @Override
    public void iter(String channel, String msg) {
        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CheckInfo[] checkInfos = ca.check();
        publish("check", JSON.toJSONString(checkInfos));
        for (CheckInfo checkInfo : checkInfos) {
            if (checkInfo.isViolated) {
                // TODO
            }
        }

        CancerObject front = ca.get("front");
        if (front.getValue() > 40) {
            publish("actor", new Actor(0, 10).toString());
        } else {
            publish("actor", new Actor(0, 0).toString());
        }
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }

}
