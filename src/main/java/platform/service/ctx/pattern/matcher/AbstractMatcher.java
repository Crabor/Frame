package platform.service.ctx.patterns.matchers;

import platform.service.ctx.patterns.types.MatcherType;
import platform.service.ctx.ctxChecker.contexts.Context;

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
