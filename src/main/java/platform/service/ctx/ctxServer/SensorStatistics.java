package platform.service.ctx.ctxServer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SensorStatistics {

    private static final class SensorStatisticsHolder {
        private static final SensorStatistics instance = new SensorStatistics();
    }

    public static SensorStatistics getInstance() {
        return SensorStatisticsHolder.instance;
    }

    private final Map<String, Long> sensorRegistrationCount;

    private final Map<String, Set<String>> sensor2Apps;

    protected SensorStatistics() {
        this.sensorRegistrationCount = new ConcurrentHashMap<>();
        this.sensor2Apps = new ConcurrentHashMap<>();
    }

    public void registerSensor(String appName, String sensorName){
        //counter
        sensorRegistrationCount.putIfAbsent(sensorName, 0L);
        sensorRegistrationCount.computeIfPresent(sensorName, (k, v) -> v + 1);
        //sensor2Apps
        sensor2Apps.computeIfAbsent(sensorName, k -> ConcurrentHashMap.newKeySet());
        sensor2Apps.get(sensorName).add(appName);
    }

    public void cancelSensor(String appName, String sensorName){
        //counter
        assert sensorRegistrationCount.containsKey(sensorName);
        sensorRegistrationCount.computeIfPresent(sensorName, (k,v) -> v - 1);
        if(sensorRegistrationCount.get(sensorName) <= 0){
            sensorRegistrationCount.remove(sensorName);
        }
        //sensor2Apps
        assert sensor2Apps.containsKey(sensorName);
        assert sensor2Apps.get(sensorName).contains(appName);
        sensor2Apps.get(sensorName).remove(appName);
        if(sensor2Apps.get(sensorName).isEmpty()){
            sensor2Apps.remove(sensorName);
        }
    }

    public Set<String> getAllRegisteredSensorSet(){
        return new HashSet<>(sensorRegistrationCount.keySet());
    }

    public Set<String> getAppNames(String sensorName){
        assert sensor2Apps.containsKey(sensorName);
        return new HashSet<>(sensor2Apps.get(sensorName));
    }


    //    protected final Map<String, Set<String>> groupId2SensorSet;
//    protected AbstractSensorStatistics(Map<String, Set<String>> groupId2SensorSet) {
//        this.groupId2SensorSet = groupId2SensorSet;
//    }
//    public abstract void registerSensor(String groupId, String sensorName);
//    public abstract String getGroupId(String sensorName);
//    public abstract Set<String> getRegisteredSensorSet();
//    public abstract void cancelSensor(String groupId, String sensorName);
}
