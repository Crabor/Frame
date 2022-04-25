package platform.service.cxt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import platform.service.cxt.Config.PlatformConfig;
import platform.service.cxt.Config.SensorConfig;

public class Configuration {
    public static List<SensorConfig> listOfSensorObj  = new ArrayList<>();
    private static PlatformConfig platformConfig;

    public static List<SensorConfig> getListOfSensorObj() {
        return listOfSensorObj;
    }

    public static PlatformConfig getPlatformConfig() {
        return platformConfig;
    }

    public static void analyzer(String configuration){
        File file = new File(configuration);
        try {
            String str = FileUtils.readFileToString(file,"UTF-8");
            JSONObject obj = JSON.parseObject(str);
            JSONObject platObj = (JSONObject) obj.get("PlatformConfiguration");
            JSONArray sensorObj = (JSONArray) obj.get("SensorConfiguration");
            //System.out.println(platObj.toJSONString());
            platformConfig  = PlatformConfig.getInstace(platObj);
            for(int i = 0; i<sensorObj.size(); i++) {
                JSONObject temp = (JSONObject) sensorObj.get(i);
                listOfSensorObj.add(new SensorConfig(temp));
                platformConfig.addSensor((temp).getString("SensorName"));
            }
            System.out.println(platformConfig.toString());
            for(int i = 0; i<listOfSensorObj.size(); i++) {
                System.out.println(listOfSensorObj.get(i).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
