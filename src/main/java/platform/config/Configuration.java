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
    private static final Map<String, AppConfig> appsConfig  = new HashMap<>();
    private static CtxServerConfig ctxServerConfig;
    private static InvServerConfig invServerConfig;
    private static ResourceConfig resourceConfig;
    private static RedisConfig redisConfig;

    public static Map<String, AppConfig> getAppsConfig() {
        return appsConfig;
    }

    public static CtxServerConfig getCtxServerConfig() {
        return ctxServerConfig;
    }

    public static InvServerConfig getInvServerConfig() {
        return invServerConfig;
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
            JSONObject ctxObj = (JSONObject) obj.get("CtxServerConfiguration");
            JSONArray appObj = (JSONArray) obj.get("AppConfiguration");
            JSONObject invObj = (JSONObject) obj.get("InvServerConfiguration");
            JSONObject resourceObj = (JSONObject) obj.get("ResourceConfiguration");
            JSONObject redisObj = (JSONObject) obj.get("RedisConfig");
            //System.out.println(ctxObj.toJSONString());
            ctxServerConfig  = CtxServerConfig.getInstance(ctxObj);
            for (int i = 0; i < appObj.size(); i++) {
                JSONObject temp = (JSONObject) appObj.get(i);
                appsConfig.put(temp.getString("appName"), new AppConfig(temp));
            }
            invServerConfig = new InvServerConfig(invObj);
            resourceConfig = new ResourceConfig(resourceObj);
            redisConfig = new RedisConfig(redisObj);
            logger.info(ctxServerConfig);
            logger.info(invServerConfig);
            logger.info(appsConfig);
            logger.info(resourceConfig);
            logger.info(redisConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resourceConfig.getSensorsConfig().forEach((sensorName, sensorConfig) -> ctxServerConfig.addSensorConfig(sensorConfig));
    }

    public static Set<String> getAppsBy(String sensorName) {
        return getResourceConfig().getSensorsConfig().get(sensorName).getApps();
    }

    public static Set<String> getSensorsBy(String appName) {
        return appsConfig.get(appName).getSensors();
    }
}
