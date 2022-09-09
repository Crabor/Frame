package platform.service.ctx.ctxChecker.INFuse.Patterns;


import platform.service.ctx.ctxChecker.INFuse.Contexts.Context;

import java.util.LinkedHashMap;

public abstract class PatternHandler {
    private final LinkedHashMap<String, Pattern> patternMap;

    public PatternHandler(){
        patternMap = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Pattern> getPatternMap() {
        return patternMap;
    }


    public abstract void buildPatterns(String patternFile) throws Exception;
    public abstract void outputPatterns();
    public abstract boolean ctxPatternMatched(Context context, Pattern pattern);
}
