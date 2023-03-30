package common.struct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class InvServiceConfig implements ServiceConfig{
    int initThro;
    int genThro;

    public InvServiceConfig() {
    }

    public void setInitThro(int initThro) {
        this.initThro = initThro;
    }

    public void setGenThro(int genThro) {
        this.genThro = genThro;
    }

    public int getInitThro() {
        return initThro;
    }

    public int getGenThro() {
        return genThro;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("init_thro", initThro);
        jo.put("gen_thro", genThro);
        return jo.toJSONString();
    }

    public static InvServiceConfig fromJSONString(String json) {
        InvServiceConfig config = new InvServiceConfig();
        JSONObject jo = JSON.parseObject(json);
        config.initThro = jo.getIntValue("init_thro");
        config.genThro = jo.getIntValue("gen_thro");
        return config;
    }

    @Override
    public String toJSONString() {
        return toString();
    }
}
