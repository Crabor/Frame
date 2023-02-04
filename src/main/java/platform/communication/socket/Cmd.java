package platform.communication.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Cmd {
    private JSONObject msgObj = null;
    private String msg = null;
    public String cmd;
    public String[] args;

    public Cmd(String cmd, String... args) {
        this.cmd = cmd;
        this.args = args;
    }

    public Cmd(String json) {
        msg = json;
        msgObj  = JSON.parseObject(json);
        cmd = msgObj.getString("cmd");
        args = msgObj.getString("args").split(" ");
    }

    public String toJSONString() {
        if (msg == null) {
            msgObj = new JSONObject(2);
            msgObj.put("cmd", cmd);
            msgObj.put("args", String.join(" ", args));
            msg = msgObj.toJSONString();
        }
        return msg;
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
