import java.util.Map;

public class platformBfunction {

    public boolean bfunc(String funcName, Map<String, Map<String, String>> vcMap) throws Exception {
        if ("isValid".equals(funcName)) {
            return isValid(vcMap);
        }
        else{
            throw new Exception("Illegal bfuncName");
        }
    }

    private boolean isValid(Map<String, Map<String, String>> vcMap){
        //vcMap: {"v1" : {"ctx_id" : "ctx_1", "": 123.213}}
        double value = Double.parseDouble(vcMap.get("v1").get("value"));
        return value < 200 && value > -200;
    }
}