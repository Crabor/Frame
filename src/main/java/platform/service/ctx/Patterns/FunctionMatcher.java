package platform.service.ctx.Patterns;

import platform.service.ctx.Contexts.Context;

import java.util.ArrayList;
import java.util.List;

public class FunctionMatcher extends AbstractMatcher{
    private final String funcName;
    private final List<String> fieldList;
    private final List<String> auxiliaryValueList;

    public FunctionMatcher(String funcName){
        this.funcName = funcName;
        this.fieldList = new ArrayList<>();
        this.auxiliaryValueList = new ArrayList<>();
    }

    @Override
    public boolean match(Context context) {
        return false;
    }

    public void addField(String field){
        this.fieldList.add(field);
    }

    public void addAuxiliaryValue(String auxiliaryValue){
        this.auxiliaryValueList.add(auxiliaryValue);
    }

    public String getFuncName() {
        return funcName;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public List<String> getAuxiliaryValueList() {
        return auxiliaryValueList;
    }
}
