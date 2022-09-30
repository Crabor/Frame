package platform.service.ctx.Patterns;

import platform.service.ctx.Contexts.Context;

import java.util.ArrayList;
import java.util.List;

public class FunctionMatcher extends AbstractMatcher{
    private final String funcName;
    private final List<String> extraArgList;

    public FunctionMatcher(String funcName){
        this.funcName = funcName;
        this.extraArgList = new ArrayList<>();
    }

    @Override
    public boolean match(Context context) {
        return false;
    }

    public void addExtraArg(String extraArg){
        this.extraArgList.add(extraArg);
    }

    public String getFuncName() {
        return funcName;
    }

    public List<String> getExtraArgList() {
        return extraArgList;
    }
}
