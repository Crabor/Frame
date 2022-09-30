package platform.service.ctx.Patterns;

import platform.service.ctx.Contexts.Context;

public abstract class AbstractMatcher {
    protected static Object mfuncInstance;
    protected String matchType;
    public abstract boolean match(Context context);
}
