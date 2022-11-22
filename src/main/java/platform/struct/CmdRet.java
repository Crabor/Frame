package platform.struct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.util.Util;

import java.util.Arrays;

public class CmdRet {
    private final JSONObject msgObj;
    private final String msg;
    public String cmd;
    public String[] args;
    public String ret;
//    private static Log logger = LogFactory.getLog(CmdRet.class);

    public CmdRet(Cmd cmd, String ret) {
        this.cmd = cmd.cmd;
        this.args = cmd.args;
        msgObj = new JSONObject(3);
        msgObj.put("cmd", cmd.cmd);
        msgObj.put("args", Util.stringArrayToString(cmd.args, " "));
        msgObj.put("ret", ret);
        msg = msgObj.toJSONString();
    }

    public CmdRet(String json) {
//        logger.warn(json);
        msg = json;
        msgObj  = JSON.parseObject(json);
        cmd = msgObj.getString("cmd");
        args = msgObj.getString("args").split(" ");
        ret = msgObj.getString("ret");
    }

    public String getCmdMsg() {
        JSONObject jo = new JSONObject(2);
        jo.put("cmd", msgObj.getString("cmd"));
        jo.put("args", msgObj.getString("args"));
        return jo.toJSONString();
    }

    public String toJSONString() {
        return msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
