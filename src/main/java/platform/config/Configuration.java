package platform.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Configuration {
    private static final Log logger = LogFactory.getLog(Configuration.class);
    private static List<SensorConfig> listOfSensorObj  = new ArrayList<>();
    private static List<AppConfig> listOfAppObj  = new ArrayList<>();
    private static PlatformConfig platformConfig;
    private static CancerServerConfig cancerServerConfig;
    private static ResourceConfig resourceConfig;
    private static int SensorLength;

    public static List<AppConfig> getListOfAppObj() {
        return listOfAppObj;
    }

    public static List<SensorConfig> getListOfSensorObj() {
        return listOfSensorObj;
    }

    public static PlatformConfig getPlatformConfig() {
        return platformConfig;
    }

    public static CancerServerConfig getCancerServerConfig() {
        return cancerServerConfig;
    }

    public static ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public static void analyzer(String configuration){
        File file = new File(configuration);
        try {
            String str = FileUtils.readFileToString(file,"UTF-8");
            JSONObject obj = JSON.parseObject(str);
            JSONObject platObj = (JSONObject) obj.get("PlatformConfiguration");
            JSONArray sensorObj = (JSONArray) obj.get("SensorConfiguration");
            JSONArray appObj = (JSONArray) obj.get("AppConfiguration");
            JSONObject cancerObj = (JSONObject) obj.get("CancerServerConfiguration");
            JSONObject resourceObj = (JSONObject) obj.get("ResourceConfiguration");
            //System.out.println(platObj.toJSONString());
            platformConfig  = PlatformConfig.getInstace(platObj);
            for(int i = 0; i<sensorObj.size(); i++) {
                JSONObject temp = (JSONObject) sensorObj.get(i);
                listOfSensorObj.add(new SensorConfig(temp));
                platformConfig.addSensor((temp).getString("SensorName"));
            }
            for (int i = 0; i < appObj.size(); i++) {
                JSONObject temp = (JSONObject) appObj.get(i);
                listOfAppObj.add(new AppConfig(temp));
            }
            cancerServerConfig = new CancerServerConfig(cancerObj);
            logger.info(platformConfig);
            for(int i = 0; i<listOfSensorObj.size(); i++) {
                logger.info(listOfSensorObj.get(i));
            }
            for(int i = 0; i<listOfAppObj.size(); i++) {
                logger.info(listOfAppObj.get(i));
            }
            logger.info(cancerServerConfig);
            resourceConfig = new ResourceConfig(resourceObj);
            logger.info(resourceConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SensorLength = listOfSensorObj.size();
        //System.out.println("SensorLength"+SensorLength);
    }

    public static int getSensorLength() {
        return SensorLength;
    }
}
