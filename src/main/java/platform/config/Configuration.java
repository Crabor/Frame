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
    private static List<AppConfig> listOfAppObj  = new ArrayList<>();
    private static PlatformConfig platformConfig;
    private static CancerServerConfig cancerServerConfig;
    private static ResourceConfig resourceConfig;

    private static RedisConfig redisConfig;
    private static int SensorLength;

    public static List<AppConfig> getListOfAppObj() {
        return listOfAppObj;
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

    public static RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public static void analyzer(String configuration){
        File file = new File(configuration);
        try {
            String str = FileUtils.readFileToString(file,"UTF-8");
            JSONObject obj = JSON.parseObject(str);
            JSONObject platObj = (JSONObject) obj.get("CtxServerConfiguration");
            JSONArray appObj = (JSONArray) obj.get("AppConfiguration");
            JSONObject cancerObj = (JSONObject) obj.get("CancerServerConfiguration");
            JSONObject resourceObj = (JSONObject) obj.get("ResourceConfiguration");
            JSONObject redisObj = (JSONObject) obj.get("RedisConfig");
            //System.out.println(platObj.toJSONString());
            platformConfig  = PlatformConfig.getInstace(platObj);
            for (int i = 0; i < appObj.size(); i++) {
                JSONObject temp = (JSONObject) appObj.get(i);
                listOfAppObj.add(new AppConfig(temp));
            }
            cancerServerConfig = new CancerServerConfig(cancerObj);
            resourceConfig = new ResourceConfig(resourceObj);
            redisConfig = new RedisConfig(redisObj);
            logger.info(platformConfig);
            logger.info(cancerServerConfig);
            logger.info(listOfAppObj);
            logger.info(resourceConfig);
            logger.info(redisConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SensorConfig> listOfSensorObj = resourceConfig.getListOfSensorObj();
        SensorLength = listOfSensorObj.size();
        for (SensorConfig sensorConfig : listOfSensorObj) {
            platformConfig.addSensor(sensorConfig.getSensorName());
        }
        //System.out.println("SensorLength"+SensorLength);
    }

    public static int getSensorLength() {
        return SensorLength;
    }
}
