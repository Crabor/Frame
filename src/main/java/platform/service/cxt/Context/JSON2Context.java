package platform.service.cxt.Context;

import com.alibaba.fastjson.JSONObject;
import platform.service.cxt.CMID.context.Context;

public class JSON2Context {
    Context context;
    JSON2Context(int index,String s){
        JSONObject object = JSONObject.parseObject(s);

    }
}
