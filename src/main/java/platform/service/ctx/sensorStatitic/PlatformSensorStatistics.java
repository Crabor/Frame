package platform.service.ctx.sensorStatitic;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BaseSensorStatistics extends AbstractSensorStatistics{
    private final Map<String, Set<String>> sensor2GroupIdSet;

    public BaseSensorStatistics() {
        super(new ConcurrentHashMap<>());
        this.sensor2GroupIdSet = new ConcurrentHashMap<>();
    }

    @Override
    public void registerSensor(String groupId, String sensorName) {
        //sensor2GroupIdSet
        sensor2GroupIdSet.computeIfAbsent(sensorName, k-> ConcurrentHashMap.newKeySet());
        Objects.requireNonNull(sensor2GroupIdSet.computeIfPresent(sensorName, (k, v) -> v)).add(groupId);
        //groupId2SensorSet
        groupId2SensorSet.computeIfAbsent(groupId, k-> ConcurrentHashMap.newKeySet());
        Objects.requireNonNull(groupId2SensorSet.computeIfPresent(groupId, (k, v) -> v)).add(sensorName);
    }

    @Override
    public String getGroupId(String sensorName) {
        assert false;
        return null;
    }

    @Override
    public Set<String> getRegisteredSensorSet() {
        return new HashSet<>(sensor2GroupIdSet.keySet());
    }


    @Override
    public void cancelSensor(String groupId, String sensorName) {
        //sensor2GroupIdSet
        assert sensor2GroupIdSet.containsKey(sensorName);
        sensor2GroupIdSet.get(sensorName).remove(groupId);
        if(sensor2GroupIdSet.get(sensorName).isEmpty()){
            sensor2GroupIdSet.remove(sensorName);
        }
        //groupId2SensorSet
        assert groupId2SensorSet.containsKey(groupId);
        groupId2SensorSet.get(groupId).remove(sensorName);
    }
}
