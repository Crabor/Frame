package platform.service.ctx.Context_bk;

import com.alibaba.fastjson.JSONObject;
import platform.service.ctx.ctxChecker.CMID.context.Context;

public class JSON2Context {
    Context context;
    JSON2Context(int index,String s){
        JSONObject object = JSONObject.parseObject(s);

    }
}
