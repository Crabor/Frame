package platform.struct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import platform.config.Configuration;
import platform.util.Util;

import java.util.Arrays;
import java.util.Collection;

public class Cmd {
    private final JSONObject msgObj;
    private final String msg;
    public String cmd;
    public String[] args;

    public Cmd(String cmd, String... args) {
        this.cmd = cmd;
        this.args = args;
        msgObj = new JSONObject(2);
        msgObj.put("cmd", cmd);
        msgObj.put("args", Util.stringArrayToString(args, " "));
        msg = msgObj.toJSONString();
    }

    public Cmd(String json) {
        msg = json;
        msgObj  = JSON.parseObject(json);
        cmd = msgObj.getString("cmd");
        args = msgObj.getString("args").split(" ");
    }

    public String toJSONString() {
        return msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
