package platform.service.ctx.ctxChecker.context;

import platform.config.CtxServerConfig;

import java.util.HashMap;
import java.util.List;
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

        Context context = (Context) o;

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

    public String toAllString(){
        return "Context{" +
        "contextId='" + contextId + '\'' +
        ", contextFields=" + contextFields +
        '}';
    }

    public String toMsgString(){
        StringBuilder stringBuilder = new StringBuilder();
        String sensorName = contextId.substring(0, contextId.lastIndexOf("_"));
        List<String> sensorFields = CtxServerConfig.getInstance().getSensorConfigMap().get(sensorName).getFieldNames();
        for(String field : sensorFields){
            stringBuilder.append(contextFields.get(field)).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

}
