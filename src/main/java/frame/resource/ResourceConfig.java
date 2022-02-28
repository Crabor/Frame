package frame.resource;

import frame.struct.sensor.Type;

import java.util.Map;

public class ResourceConfig {
    public Map<String, Type> sensors;
    public ResourceSensor resourceSensor;
    public ResourceActor resourceActor;

    // TODO:实现从文本中读取resource配置
    public static ResourceConfig readConfig(String filename) {
        return null;
    }
}
