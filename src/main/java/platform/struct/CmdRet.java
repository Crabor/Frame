package platform.struct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class CmdRet {
    public String cmd;
    public String[] args;
    public String[] rets;
    private static Log logger = LogFactory.getLog(CmdRet.class);

    public CmdRet(String json) {
//        logger.warn(json);
        JSONObject jo = JSON.parseObject(json);
        cmd = jo.getString("cmd");
        args = jo.getString("args").split(" ");
        rets = jo.getString("rets").split(" ");
    }
}
