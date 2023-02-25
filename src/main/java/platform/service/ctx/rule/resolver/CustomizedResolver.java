package platform.service.ctx.rule.resolver;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomizedResolver extends AbstractResolver{
    private String funcName;
    private Object rfuncInstance;

    public CustomizedResolver(Object rfuncInstance) {
        this.resolverStrategy = ResolverStrategy.customized;
        this.rfuncInstance = rfuncInstance;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Object getRfuncInstance() {
        return rfuncInstance;
    }

    public void setRfuncInstance(Object rfuncInstance) {
        this.rfuncInstance = rfuncInstance;
    }

    @Override
    public Set<Map.Entry<String, HashMap<String, String>>> resolve(Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> linkSet) {
        Set<Map.Entry<String, HashMap<String, String>>> flatContextSet = new HashSet<>();
        //TODO
        return flatContextSet;
    }
}
