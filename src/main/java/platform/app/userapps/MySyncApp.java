package platform.app.userapps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.app.AbstractSyncApp;
import platform.service.inv.CancerArray;
import platform.service.inv.CancerObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.struct.Actor;

public class MySyncApp extends AbstractSyncApp {
    private static final Log logger = LogFactory.getLog(MySyncApp.class);
    double ratio = 1;

    public class CheckStates {
        public CheckState left;
        public CheckState right;
        public CheckState front;
        public CheckState back;

        public CheckState getLeft() {
            return left;
        }

        public CheckState getRight() {
            return right;
        }

        public CheckState getFront() {
            return front;
        }

        public CheckState getBack() {
            return back;
        }
    }

    @Override
    public void iter(String channel, String msg) {
        logger.debug("app recv: " + msg);
        Actor actor = new Actor(2, 0, 0);
        CheckStates checkStates = new CheckStates();

        CancerArray ca = CancerArray.fromJsonObjectString(msg);
        CheckInfo[] checkInfos = ca.check();
        for (CheckInfo checkInfo : checkInfos) {
            if (checkInfo.name.equals("left")) {
                logger.debug("check state: " + checkInfo.checkState);
                if (checkInfo.checkState == CheckState.INV_VIOLATED) {
                    actor.setYSpeed(ratio);
                    ratio += 0.01;
                } else {
                    ratio = 1;
                }
            }
            switch (checkInfo.name) {
                case "left":
                    checkStates.left = checkInfo.checkState;
                    break;
                case "right":
                    checkStates.right = checkInfo.checkState;
                    break;
                case "front":
                    checkStates.front = checkInfo.checkState;
                    break;
                case "back":
                    checkStates.back = checkInfo.checkState;
                    break;
            }
        }
        publish("check", JSON.toJSONString(checkInfos));
        publish("checkState", JSON.toJSONString(checkStates));
        publish("actor", JSON.toJSONString(actor));
    }

    @Override
    public void onSubscribed(String s, long l) {

    }

    @Override
    public void onUnsubscribed(String s, long l) {

    }
}
