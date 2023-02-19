package common.struct;

import com.alibaba.fastjson.JSON;

public class InvServiceConfig implements ServiceConfig{

    @Override
    public String toString() {
        return "InvServiceConfig{}";
    }

    public static InvServiceConfig fromJSONString(String json) {
        return JSON.parseObject(json, InvServiceConfig.class);
    }
}
