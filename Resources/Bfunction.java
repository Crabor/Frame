import java.util.Map;

public class Bfunction {

    public boolean bfunc(String funcName, Map<String, Map<String, String>> vcMap) throws Exception {
        if ("isValid".equals(funcName)) {
            return isValid(vcMap);
        }
        else if("sz_loc_range".equals(funcName)){
            return sz_loc_range(vcMap);
        }
        else if("same".equals(funcName)){
            return same(vcMap);
        } else if ("sz_loc_close".equals(funcName)) {
            return sz_loc_close(vcMap);
        }
        else if("sz_spd_close".equals(funcName)){
            return sz_spd_close(vcMap);
        }
        throw new Exception("Illegal bfuncName");
    }

    private boolean sz_loc_range(Map<String, Map<String, String>> vcMap){
        String[] parts = String.valueOf(vcMap.get("v1").get("value")).split(";");
        double lon = Double.parseDouble(parts[1]);
        double lat = Double.parseDouble(parts[2]);
        // The longitude and latitude should be in [112, 116] and [20, 24], respectively
        return !(lon < 112.0) && !(lon > 116.0) && !(lat < 20.0) && !(lat > 24.0);
    }

    private boolean same(Map<String, Map<String, String>> vcMap) {
        String[] parts1 = String.valueOf(vcMap.get("v1").get("value")).split(";");
        String[] parts2 = String.valueOf(vcMap.get("v2").get("value")).split(";");
        return parts1[0].equals(parts2[0]);
    }

    private boolean sz_loc_close(Map<String, Map<String, String>> vcMap) {
        String[] parts1 = String.valueOf(vcMap.get("v1").get("value")).split(";");
        String[] parts2 = String.valueOf(vcMap.get("v2").get("value")).split(";");

        double lon1 = Double.parseDouble(parts1[1]);
        double lat1 = Double.parseDouble(parts1[2]);
        double lon2 = Double.parseDouble(parts2[1]);
        double lat2 = Double.parseDouble(parts2[2]);

        double distSq = (lon2 - lon1) * (lon2 - lon1) + (lat2 - lat1) * (lat2 - lat1);
        // The distance should be no more than 0.001 as 'close'
        return !(distSq > 0.001 * 0.001);
    }

    private boolean sz_spd_close(Map<String, Map<String, String>> vcMap)  {
        String[] parts1 = String.valueOf(vcMap.get("v1").get("value")).split(";");
        String[] parts2 = String.valueOf(vcMap.get("v2").get("value")).split(";");

        int speed1 = Integer.parseInt(parts1[3]);
        int speed2 = Integer.parseInt(parts2[3]);
        // The difference should be no more than 50 (as 'close')
        return Math.abs(speed2 - speed1) <= 50;
    }

    private boolean isValid(Map<String, Map<String, String>> vcMap){
        //vcMap: {"v1" : {"ctx_id" : "ctx_1", "value": 123.213}}
        double value = Double.parseDouble(vcMap.get("v1").get("value"));
        return value < 200 && value > -200;
    }
}