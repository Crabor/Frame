import java.util.Map;
import java.util.List;

public class mfuncs {

    public boolean mfunc(final String funcName, final Map<String, String> ctxFields, final List<String> extraArgumentList) throws Exception {
        if ("onService".equals(funcName)) {
            return onService(ctxFields, extraArgumentList);
        }
        else{
            throw new Exception("Illegal bfuncName");
        }
    }

    private boolean onService(final Map<String, String> ctxFields, final List<String> extraArgumentList){
        String status = ctxFields.get("state");
        return !status.equalsIgnoreCase(extraArgumentList.get(0));
    }
}