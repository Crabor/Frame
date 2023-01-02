package platform.service.ctx.rule.resolver;

import platform.service.ctx.ctxChecker.constraint.runtime.Link;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.*;

public abstract class AbstractResolver {
    protected ResolverStrategy resolverStrategy;
    protected final Set<Integer> group;
    protected int priority;

    protected AbstractResolver() {
        this.group = new HashSet<>();
    }

    public abstract Set<Map.Entry<String, HashMap<String, String>>> resolve(Set<HashMap<String, Map.Entry<String, HashMap<String, String>>>> linkSet);

    public void addGroup(String groupsString){
        String[] groups = groupsString.split(",");
        for(String groupString : groups){
            this.group.add(Integer.parseInt(groupString));
        }
    }

    public Set<Integer> getGroup() {
        return group;
    }

    public void setResolverStrategy(ResolverStrategy resolverStrategy) {
        this.resolverStrategy = resolverStrategy;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ResolverStrategy getResolverStrategy() {
        return resolverStrategy;
    }

    public int getPriority() {
        return priority;
    }
}
