package platform.service.ctx.item;

import platform.service.ctx.ctxChecker.context.Context;

import java.util.HashSet;
import java.util.Set;

public class Item {
    private final long index;
    private Context context;

    private final Set<String> violatedRules;

    private String state;

    public Item(long index) {
        this.index = index;
        this.violatedRules = new HashSet<>();
    }

    public void addContext(Context context){ this.context = context;}

    public void addViolatedRule(String ruleId){
        this.violatedRules.add(ruleId);
    }

    public void setState(String state){
        this.state = state;
    }

    public long getIndex() {
        return index;
    }

    public Context getContext() {
        return context;
    }

    public Set<String> getViolatedRules() {
        return violatedRules;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Item{" +
                "index=" + index +
                ", context=" + context +
                '}';
    }
}
