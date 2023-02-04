package platform.communication.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class CmdRet {
    private JSONObject msgObj = null;
    private String msg = null;
    public String cmd;
    public String[] args;
    public String ret;
//    private static Log logger = LogFactory.getLog(CmdRet.class);

    public CmdRet(Cmd cmd, String ret) {
        this.cmd = cmd.cmd;
        this.args = cmd.args;
        this.ret = ret;
//        msgObj = new JSONObject(3);
//        msgObj.put("cmd", cmd.cmd);
//        msgObj.put("args", Util.stringArrayToString(cmd.args, " "));
//        msgObj.put("ret", ret);
//        msg = msgObj.toJSONString();
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
        jo.put("cmd", cmd);
        jo.put("args", String.join(" ", args));
        return jo.toJSONString();
    }

    public String toJSONString() {
        if (msg == null) {
            msgObj = new JSONObject(3);
            msgObj.put("cmd", cmd);
            msgObj.put("args", String.join(" ", args));
            msgObj.put("ret", ret);
            msg = msgObj.toJSONString();
        }
        return msg;
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
