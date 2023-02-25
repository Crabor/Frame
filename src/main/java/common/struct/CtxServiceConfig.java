package common.struct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.struct.enumeration.CtxValidator;
import common.util.Util;

import java.util.HashMap;
import java.util.Map;

public class CtxServiceConfig implements ServiceConfig{
    private Map<String, String> config;

    public CtxServiceConfig() {
        config = new HashMap<>();
    }

    public int size() {
        return config.size();
    }

    public boolean setCtxResources(String ruleFile, String patternFile, String bfuncFile, String mfuncFile, String rfuncFile) {

        String ruleFileContent = ruleFile == null ? null : Util.readFileContent(ruleFile);
        String patternFileContent = patternFile == null ? null : Util.readFileContent(patternFile);
        String bfuncFileContent = bfuncFile == null ? null : Util.readFileContent(bfuncFile);
        String mfuncFileContent = mfuncFile == null ? null : Util.readFileContent(mfuncFile);
        String rfuncFileContent = rfuncFile == null ? null : Util.readFileContent(rfuncFile);

        if (ruleFileContent != null && patternFileContent != null && bfuncFileContent != null) {
            config.put("rule_file_content", ruleFileContent);
            config.put("pattern_file_content", patternFileContent);
            config.put("bfunc_file_content", bfuncFileContent);
            config.put("mfunc_file_content", mfuncFileContent);
            config.put("rfunc_file_content", rfuncFileContent);
            return true;
        } else {
            return false;
        }
    }

    public boolean setCtxValidator(CtxValidator ctxValidator) {
        config.put("ctx_validator", ctxValidator.toString());
        return true;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(config);
    }

    public static CtxServiceConfig fromJSONString(String json) {
        //TODO:检查是否有效
        CtxServiceConfig config = new CtxServiceConfig();
        JSONObject jo = JSON.parseObject(json);
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            config.config.put(entry.getKey(), entry.getValue().toString());
        }
        return config;
    }

    public CtxValidator getCtxValidator() {
        if (config.containsKey("ctx_validator")) {
            return CtxValidator.fromString(config.get("ctx_validator"));
        } else {
            return null;
        }
    }

    public String getRuleFileContent() {
        return config.getOrDefault("rule_file_content", null);
    }

    public String getPatternFileContent() {
        return config.getOrDefault("pattern_file_content", null);
    }

    public String getBfuncFileContent() {
        return config.getOrDefault("bfunc_file_content", null);
    }

    public String getMfuncFileContent() {
        return config.getOrDefault("mfunc_file_content", null);
    }

    public String getRfuncFileContent() {
        return config.getOrDefault("rfunc_file_content", null);
    }
}
