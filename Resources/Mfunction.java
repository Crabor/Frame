import java.util.Map;

public class Mfunction {

    public boolean mfunc(final String funcName, final Map<String, String> ctxFields, final List<String> extraArgumentList) throws Exception {
        if ("alwaysTrue".equals(funcName)) {
            return alwaysTrue(ctxFields, extraArgumentList);
        }
        throw new Exception("Illegal bfuncName");
    }

    private boolean alwaysTrue(final Map<String, String> ctxFields, final List<String> extraArgumentList){
        return true;
    }
}