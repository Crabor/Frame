package common.struct;

import common.struct.enumeration.CtxValidator;

public class CtxServiceConfig implements ServiceConfig{

    public boolean setCtxResources(String ruleFile, String patternFile, String bfuncFile, String mfuncFile, String rfuncFile) {
        return true;
    }

    public boolean setCtxValidator(CtxValidator ctxValidator) {
        return true;
    }
}
