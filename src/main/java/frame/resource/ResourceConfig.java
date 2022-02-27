package frame.resource;

import frame.resource.actor.ActorSrc;
import frame.resource.sensor.SensorSrc;
import frame.resource.sensor.Type;

import java.util.Map;

public class ResourceConfig {
    public Map<String, Type> sensors;
    public SensorSrc sensorSrc;
    public ActorSrc actorSrc;

    // TODO:实现从文本中读取resource配置
    public static ResourceConfig readConfig(String filename) {
        return null;
    }
}
