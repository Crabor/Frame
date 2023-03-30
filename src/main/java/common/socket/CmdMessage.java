package common.socket;

import com.alibaba.fastjson.JSONObject;

public class CmdMessage {
    private JSONObject jo = null;
    private String json = null;
    public String cmd;
    public String message;

    public CmdMessage(String cmd, String message) {
        this.cmd = cmd;
        this.message = message;
    }

    public CmdMessage(String json) {
        this.json = json;
        jo = JSONObject.parseObject(json);
        cmd = jo.getString("cmd");
        message = jo.getString("message");
    }

    public String toJSONString() {
        if (json == null) {
            jo = new JSONObject(2);
            jo.put("cmd", cmd);
            jo.put("message", message);
            json = jo.toJSONString();
        }
        return json;
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
