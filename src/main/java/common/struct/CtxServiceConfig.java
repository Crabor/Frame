package common.struct;

import com.alibaba.fastjson.JSON;
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
        String ruleFileContent = Util.readFileContent(ruleFile);
        String patternFileContent = Util.readFileContent(patternFile);
        String bfuncFileContent = Util.readFileContent(bfuncFile);
        String mfuncFileContent = Util.readFileContent(mfuncFile);
        String rfuncFileContent = Util.readFileContent(rfuncFile);

        if (ruleFileContent != null && patternFileContent != null && bfuncFileContent != null && mfuncFileContent != null && rfuncFileContent != null) {
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
        return JSON.parseObject(json, CtxServiceConfig.class);
    }
}
