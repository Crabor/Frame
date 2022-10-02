package platform.service.ctx.Contexts;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private String contextId;

    private final Map<String, String> contextFields;

    public Context() {
        this.contextFields = new HashMap<>();
    }

    public String getContextId() {
        return contextId;
    }

    public Map<String, String> getContextFields() {
        return contextFields;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        platform.service.ctx.Contexts.Context context = (platform.service.ctx.Contexts.Context) o;

        return contextId.equals(context.contextId);
    }

    @Override
    public int hashCode() {
        return contextId.hashCode();
    }

    @Override
    public String toString() {
        return "ctx_id=" + contextId;
    }
}
