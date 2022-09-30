package platform.service.ctx.Patterns;

import platform.service.ctx.Contexts.Context;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKeyMatcher extends AbstractMatcher{
    private final String field;
    private final List<String> optionalValueList;

    public PrimaryKeyMatcher(String field) {
        this.field = field;
        this.optionalValueList = new ArrayList<>();
    }

    @Override
    public boolean match(Context context) {
        return false;
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
}
