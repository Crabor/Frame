package common.socket;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;

import java.util.List;

public class CmdMessageGrpIds {
    private JSONObject jo = null;
    private String json = null;
    public String cmd;
    public String message;
    public List<Integer> grpIds;

    public CmdMessageGrpIds(CmdMessage cm, List<Integer> grpIds) {
        this.cmd = cm.cmd;
        this.message = cm.message;
        this.grpIds = grpIds;
    }

    public CmdMessageGrpIds(String cmd, String message, List<Integer> grpIds) {
        this.cmd = cmd;
        this.message = message;
        this.grpIds = grpIds;
    }

    public CmdMessageGrpIds(String json) {
        this.json = json;
        jo = JSONObject.parseObject(json);
        cmd = jo.getString("cmd");
        message = jo.getString("message");
        grpIds = Util.toIntegerList(jo.getString("grpIds"), " ");
    }

    public CmdMessage getCmdMessage() {
        return new CmdMessage(cmd, message);
    }

    public String toJSONString() {
        if (json == null) {
            jo = new JSONObject(3);
            jo.put("cmd", cmd);
            jo.put("message", message);
            jo.put("grpIds", Util.toString(grpIds, " "));
            json = jo.toJSONString();
        }
        return json;
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
