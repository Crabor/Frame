package platform.service.ctx.pattern.matcher;

import platform.service.ctx.pattern.types.MatcherType;
import platform.service.ctx.ctxChecker.context.Context;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKeyMatcher extends AbstractMatcher{
    private final String field;
    private final List<String> optionalValueList;

    public PrimaryKeyMatcher(String field) {
        this.matcherType = MatcherType.PRIMARY_KEY;
        this.mfuncInstance = null;
        this.field = field;
        this.optionalValueList = new ArrayList<>();
    }

    @Override
    public boolean match(Context context) {
        String contextValue = context.getContextFields().get(field);
        return optionalValueList.contains(contextValue);
    }


    public void addOptionalValue(String optionalValue){
        this.optionalValueList.add(optionalValue);
    }

    public String getField() {
        return field;
    }

    public List<String> getOptionalValueList() {
        return optionalValueList;
    }


    @Override
    public String toString() {
        return "PrimaryKeyMatcher{" +
                "field='" + field + '\'' +
                ", optionalValueList=" + optionalValueList +
                '}';
    }
}
