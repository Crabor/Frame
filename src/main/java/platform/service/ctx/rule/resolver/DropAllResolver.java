package platform.service.ctx.rule.resolver;

import platform.service.ctx.ctxChecker.context.Context;

import java.util.*;

public class DropAllResolver extends AbstractResolver{

    public DropAllResolver() {
        this.resolverStrategy = ResolverStrategy.drop_all;
    }

    @Override
    public Set<Map.Entry<String, HashMap<String, String>>> resolve(Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> linkSet) {
        Set<Map.Entry<String, HashMap<String, String>>> flatContextSet = new HashSet<>();
        //TODO
        return flatContextSet;
    }


}
