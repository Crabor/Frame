import java.util.Map;
import java.util.List;

public class Mfunction {

    public boolean mfunc(final String funcName, final Map<String, String> ctxFields, final List<String> extraArgumentList) throws Exception {
        if ("onService".equals(funcName)) {
            return onService(ctxFields, extraArgumentList);
        }
        throw new Exception("Illegal bfuncName");
    }

    private boolean alwaysTrue(final Map<String, String> ctxFields, final List<String> extraArgumentList){
        return true;
    }

    private boolean onService(final Map<String, String> ctxFields, final List<String> extraArgumentList){
        String status = ctxFields.get("status");
        return !status.equalsIgnoreCase("0");
    }
}