package platform.config;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorConfig {
    private String SensorType;
    private String SensorName;
    private List<String> fieldNames;
    private boolean isValid;
    private int SensorFreq; // per second;
    private String IPAddress;
    private int port;

    public SensorConfig(JSONObject object){
        try {
            SensorType = object.getString("SensorType");
        } catch (NullPointerException e) {
            SensorType = "String";
        }
        SensorName = object.getString("SensorName");
        try{
            fieldNames = Arrays.asList(object.getString("fieldNames").split(","));
        } catch (NullPointerException e){
            fieldNames = new ArrayList<>();
            fieldNames.add(object.getString("SensorName"));
        }

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

    public List<String> getFieldNames() {
        return fieldNames;
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

    @Override
    public String toString() {
        return "SensorConfig{" +
                "SensorType='" + SensorType + '\'' +
                ", SensorName='" + SensorName + '\'' +
                ", fieldNames=" + fieldNames +
                ", isValid=" + isValid +
                ", SensorFreq=" + SensorFreq +
                ", IPAddress='" + IPAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
