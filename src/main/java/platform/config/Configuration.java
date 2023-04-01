package platform.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Configuration {
    private static final Log logger = LogFactory.getLog(Configuration.class);
    private static final Map<String, AppConfig> appsConfig  = new HashMap<>();
    private static CtxServerConfig ctxServerConfig;
//    private static InvServerConfig invServerConfig;
    private static ResourceConfig resourceConfig;
    private static RedisConfig redisConfig;
    private static UDPConfig udpConfig;
    private static TCPConfig tcpConfig;

    public static Map<String, AppConfig> getAppsConfig() {
        return appsConfig;
    }

    public static CtxServerConfig getCtxServerConfig() {
        return ctxServerConfig;
    }

//    public static InvServerConfig getInvServerConfig() {
//        return invServerConfig;
//    }

    public static ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public static RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public static UDPConfig getUdpConfig() {
        return udpConfig;
    }

    public static TCPConfig getTcpConfig() {
        return tcpConfig;
    }

    public static void analyzer(String configuration){
        File file = new File(configuration);
        try {
            String str = FileUtils.readFileToString(file,"UTF-8");
            JSONObject obj = JSON.parseObject(str);
            JSONObject ctxObj = (JSONObject) obj.get("CtxServerConfiguration");
//            JSONArray appObj = (JSONArray) obj.get("AppConfiguration");
//            JSONObject invObj = (JSONObject) obj.get("InvServerConfiguration");
//            JSONObject resourceObj = (JSONObject) obj.get("ResourceConfiguration");
            JSONObject redisObj = (JSONObject) obj.get("RedisConfig");
//            JSONObject udpObj = (JSONObject) obj.get("UDPConfig");
            JSONObject tcpObj = (JSONObject) obj.get("TCPConfig");
            //System.out.println(ctxObj.toJSONString());
            ctxServerConfig  = CtxServerConfig.getInstance(ctxObj);
//            for (int i = 0; i < appObj.size(); i++) {
//                JSONObject temp = (JSONObject) appObj.get(i);
//                appsConfig.put(temp.getString("appName"), new AppConfig(temp));
//            }
//            invServerConfig = new InvServerConfig(invObj);
            resourceConfig = new ResourceConfig();
            redisConfig = new RedisConfig(redisObj);
//            udpConfig = new UDPConfig(udpObj);
            tcpConfig = new TCPConfig(tcpObj);
            logger.info(ctxServerConfig);
//            logger.info(invServerConfig);
//            logger.info(appsConfig);
//            logger.info(resourceConfig);
            logger.info(redisConfig);
//            logger.info(udpConfig);
            logger.info(tcpConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        resourceConfig.getSensorsConfig().forEach((sensorName, sensorConfig) -> ctxServerConfig.addSensorConfig(sensorConfig));
    }

    public static Set<AppConfig> getAppsBySensorName(String sensorName) {
        return getResourceConfig().getSensorsConfig().get(sensorName).getApps();
    }

    public static Set<AppConfig> getAppsByActorName(String actorName) {
        return getResourceConfig().getActorsConfig().get(actorName).getApps();
    }

    public static Set<SensorConfig> getSensorsByAppName(String appName) {
        return appsConfig.get(appName).getSensors();
    }

    public static Set<ActorConfig> getActorsByAppName(String appName) {
        return appsConfig.get(appName).getActors();
    }

    public static Set<String> getRegisteredSensors() {
        Set<String> registeredSensors = new HashSet<>(Set.of());
        getResourceConfig().getSensorsConfig().values().forEach(sensorConfig -> {
            if (!sensorConfig.getApps().isEmpty()) {
                registeredSensors.add(sensorConfig.getSensorName());
            }
        });
        return registeredSensors;
    }

//    public static boolean isSensorExists(String sensorName) {
//        return Configuration.getResourceConfig().getSensorsConfig().containsKey(sensorName);
//    }
//
//    public static void addSensorConfig(String sensorName, String sensorType, String fieldNames) {
//        SensorConfig config = new SensorConfig(sensorName, sensorType, fieldNames);
//        Configuration.getResourceConfig().getSensorsConfig().put(sensorName, config);
//        ctxServerConfig.addSensorConfig(config);
//    }
}
