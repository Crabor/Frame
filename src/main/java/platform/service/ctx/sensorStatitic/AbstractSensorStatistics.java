package platform.service.ctx.sensorStatitic;

import java.util.Map;
import java.util.Set;

public abstract class AbstractSensorStatistics {

    protected final Map<String, Set<String>> app2SensorSet;

    protected AbstractSensorStatistics(Map<String, Set<String>> app2SensorSet) {
        this.app2SensorSet = app2SensorSet;
    }

    public abstract void registerSensor(String appName, String sensorName);
    public abstract void cancelSensor(String appName, String sensorName);
    public abstract Set<String> getRegisteredSensorSet();

//    protected final Map<String, Set<String>> groupId2SensorSet;
//    protected AbstractSensorStatistics(Map<String, Set<String>> groupId2SensorSet) {
//        this.groupId2SensorSet = groupId2SensorSet;
//    }
//    public abstract void registerSensor(String groupId, String sensorName);
//    public abstract String getGroupId(String sensorName);
//    public abstract Set<String> getRegisteredSensorSet();
//    public abstract void cancelSensor(String groupId, String sensorName);
}
