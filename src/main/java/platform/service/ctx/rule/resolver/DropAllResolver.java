package platform.service.ctx.rule.resolver;

import java.util.*;

public class DropAllResolver extends AbstractResolver{

    public DropAllResolver() {
        this.resolverStrategy = ResolverStrategy.DROP_ALL;
    }

    @Override
    public Set<Map.Entry<String, HashMap<String, String>>> resolve(Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> linkSet) {
        Set<Map.Entry<String, HashMap<String, String>>> flatContextSet = new HashSet<>();
        //TODO
        return flatContextSet;
    }


}
