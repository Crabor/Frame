package platform.service.ctx.sensorStatitic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlatformSensorStatistics extends AbstractSensorStatistics{
    private final Map<String, Long> sensorRegistrationCounter;

    public PlatformSensorStatistics() {
        super(new ConcurrentHashMap<>());
        this.sensorRegistrationCounter = new ConcurrentHashMap<>();
    }

    @Override
    public void registerSensor(String appName, String sensorName) {
        //app2SensorSet
        app2SensorSet.computeIfAbsent(appName, k -> new HashSet<>());
        app2SensorSet.get(appName).add(sensorName);
        //counter
        sensorRegistrationCounter.putIfAbsent(sensorName, 0L);
        sensorRegistrationCounter.computeIfPresent(sensorName, (k,v)-> v + 1);
    }

    @Override
    public void cancelSensor(String appName, String sensorName) {
        //app2SensorSet
        assert app2SensorSet.containsKey(appName);
        app2SensorSet.get(appName).remove(sensorName);
        //counter
        assert sensorRegistrationCounter.containsKey(sensorName);
        sensorRegistrationCounter.computeIfPresent(sensorName, (k,v) -> v - 1);
        if(sensorRegistrationCounter.get(sensorName) <= 0){
            sensorRegistrationCounter.remove(sensorName);
        }
    }

    @Override
    public Set<String> getRegisteredSensorSet() {
        return new HashSet<>(sensorRegistrationCounter.keySet());
    }

    //    private final Map<String, Set<String>> sensor2GroupIdSet;
//    public PlatformSensorStatistics() {
//        super(new ConcurrentHashMap<>());
//        this.sensor2GroupIdSet = new ConcurrentHashMap<>();
//    }
//    @Override
//    public void registerSensor(String groupId, String sensorName) {
//        //sensor2GroupIdSet
//        sensor2GroupIdSet.computeIfAbsent(sensorName, k-> ConcurrentHashMap.newKeySet());
//        Objects.requireNonNull(sensor2GroupIdSet.computeIfPresent(sensorName, (k, v) -> v)).add(groupId);
//        //groupId2SensorSet
//        groupId2SensorSet.computeIfAbsent(groupId, k-> ConcurrentHashMap.newKeySet());
//        Objects.requireNonNull(groupId2SensorSet.computeIfPresent(groupId, (k, v) -> v)).add(sensorName);
//    }
//    @Override
//    public String getGroupId(String sensorName) {
//        assert false;
//        return null;
//    }
//    @Override
//    public Set<String> getRegisteredSensorSet() {
//        return new HashSet<>(sensor2GroupIdSet.keySet());
//    }
//    @Override
//    public void cancelSensor(String groupId, String sensorName) {
//        //sensor2GroupIdSet
//        assert sensor2GroupIdSet.containsKey(sensorName);
//        sensor2GroupIdSet.get(sensorName).remove(groupId);
//        if(sensor2GroupIdSet.get(sensorName).isEmpty()){
//            sensor2GroupIdSet.remove(sensorName);
//        }
//        //groupId2SensorSet
//        assert groupId2SensorSet.containsKey(groupId);
//        groupId2SensorSet.get(groupId).remove(sensorName);
//    }
}
