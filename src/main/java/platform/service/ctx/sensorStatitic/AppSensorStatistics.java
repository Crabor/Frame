package platform.service.ctx.sensorStatitic;

import java.util.*;

public class AppSensorStatistics extends AbstractSensorStatistics{

    private final String appName;

    public AppSensorStatistics(String appName) {
        super(new HashMap<>());
        this.appName = appName;
        this.app2SensorSet.put(appName, new HashSet<>());
    }

    @Override
    public void registerSensor(String appName, String sensorName) {
        assert app2SensorSet.containsKey(appName);
        app2SensorSet.get(appName).add(sensorName);
    }

    @Override
    public void cancelSensor(String appName, String sensorName) {
        assert app2SensorSet.containsKey(appName);
        app2SensorSet.get(appName).remove(sensorName);
    }

    @Override
    public Set<String> getRegisteredSensorSet() {
        return new HashSet<>(app2SensorSet.get(appName));
    }

    //    private final Map<String, String> sensor2GroupId;
//    public AppSensorStatistics() {
//        super(new HashMap<>());
//        this.sensor2GroupId = new HashMap<>();
//    }
//    @Override
//    public void registerSensor(String groupId, String sensorName) {
//        //sensor2GroupId
//        sensor2GroupId.put(sensorName, groupId);
//        //groupId2SensorSet
//        groupId2SensorSet.computeIfAbsent(groupId, k-> new HashSet<>());
//        Objects.requireNonNull(groupId2SensorSet.computeIfPresent(groupId, (k, v) -> v)).add(sensorName);
//    }
//    @Override
//    public String getGroupId(String sensorName) {
//        return sensor2GroupId.get(sensorName);
//    }
//    @Override
//    public Set<String> getRegisteredSensorSet() {
//        return new HashSet<>(sensor2GroupId.keySet());
//    }
//    @Override
//    public void cancelSensor(String groupId, String sensorName) {
//        //sensor2GroupId
//        assert sensor2GroupId.containsKey(sensorName);
//        sensor2GroupId.remove(sensorName);
//        //groupId2SensorSet
//        assert groupId2SensorSet.containsKey(groupId);
//        groupId2SensorSet.get(groupId).remove(sensorName);
//    }
}
