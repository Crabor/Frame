package platform.service.cxt.Config;

import com.alibaba.fastjson.JSONObject;

public class SensorConfig {
    private String SensorType;
    private String SensorName;
    private boolean isValid;
    private int SensorFreq; // per second;
    private String IPAddress;
    private int port;

    public SensorConfig(JSONObject object){
        SensorType = object.getString("SensorType");
        SensorName = object.getString("SensorName");
//        isValid = object.getBoolean("isValid");
//        SensorFreq = object.getIntValue("SensorFreq");
//        IPAddress = object.getString("IPAddress");
//        port = object.getIntValue("Port");
    }

    public String getSensorType() {
        return SensorType;
    }

    public String getSensorName() {
        return SensorName;
    }

    public boolean isValid() {
        return isValid;
    }

    public int getSensorFreq() {
        return SensorFreq;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getPort() {
        return port;
    }

    public String toString(){
        return "SensorType = " + SensorType
                + ", SensorName = " + SensorName
                + ", isValid = " + isValid
                + ", IPAddress = " + IPAddress
                + ", port = " + port
                + ", SensorFreq = " + SensorFreq;
    }
}
