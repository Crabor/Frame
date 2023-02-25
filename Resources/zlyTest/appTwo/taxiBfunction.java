import java.util.Map;

public class taxiBfunction {

    public boolean bfunc(String funcName, Map<String, Map<String, String>> vcMap) throws Exception {
        if("one-layer".equals(funcName)){
            return oneLayer(vcMap);
        }
        else if("two-layer".equals(funcName)){
            return twoLayer(vcMap);
        }
        else if("isValid".equals(funcName)){
            return isValid(vcMap);
        }
        else{
            throw new Exception("Illegal bfuncName");
        }
    }

    private boolean isValid(Map<String, Map<String, String>> vcMap){
        double value = Double.parseDouble(vcMap.get("v1").get("value"));
        return value < 100 && value > -100;
    }

    private boolean oneLayer(Map<String, Map<String, String>> vcMap){
        String taxiId = vcMap.get("v1").get("taxiId");
        return taxiId.endsWith("0");
    }

    private boolean twoLayer(Map<String, Map<String, String>> vcMap){
        String taxiId1 = vcMap.get("v1").get("taxiId");
        String taxiId2 = vcMap.get("v2").get("taxiId");
        return taxiId1.charAt(taxiId1.length() - 1) == taxiId2.charAt(taxiId2.length() - 1);
    }

}