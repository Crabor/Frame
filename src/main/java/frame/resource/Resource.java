package frame.resource;

import frame.struct.sensor.Type;

import java.util.Map;

public class Resource {
//    private List<Sensor> sensors = new ArrayList<>();
//    private Actor actor;
    private Map<String, Type> sensors;
    private ResourceSensor resourceSensor;
    private ResourceActor resourceActor;

    public void Resource(ResourceConfig config) {
//        config.sensors.forEach((name, type) -> {
//            sensors.add(new Sensor(name, type));
//        });
        sensors = config.sensors;
        resourceSensor = config.resourceSensor;
        resourceActor = config.resourceActor;
    }
}
