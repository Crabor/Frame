import java.util.Map;

public class bfuncs {

    public boolean bfunc(String funcName, Map<String, Map<String, String>> vcMap) throws Exception {
        if("two-layer".equals(funcName)){
            return twoLayer(vcMap);
        }
        else{
            throw new Exception("Illegal bfuncName");
        }
    }


    private boolean twoLayer(Map<String, Map<String, String>> vcMap){
        String taxiId1 = vcMap.get("v1").get("carId");
        String taxiId2 = vcMap.get("v2").get("carId");
        return taxiId1.charAt(taxiId1.length() - 1) == taxiId2.charAt(taxiId2.length() - 1);
    }

}