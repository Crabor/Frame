package platform.service.ctx.pattern.matcher;

import platform.service.ctx.pattern.types.MatcherType;
import platform.service.ctx.ctxChecker.context.Context;

public abstract class AbstractMatcher {
    protected Object mfuncInstance;
    protected MatcherType matcherType;
    public abstract boolean match(Context context);

    public MatcherType getMatcherType() {
        return matcherType;
    }

    public Object getMfuncInstance() {
        return mfuncInstance;
    }
}
