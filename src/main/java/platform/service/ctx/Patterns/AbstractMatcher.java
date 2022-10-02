package platform.service.ctx.Patterns;

import platform.service.ctx.Contexts.Context;
import platform.service.ctx.Patterns.Types.MatcherType;

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
