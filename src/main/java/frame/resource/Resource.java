package frame.resource;

import frame.resource.actor.Actor;
import frame.resource.actor.ActorSrc;
import frame.resource.sensor.Sensor;
import frame.resource.sensor.SensorSrc;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    private List<Sensor> sensors = new ArrayList<>();
    private Actor actor;
    private SensorSrc sensorSrc;
    private ActorSrc actorSrc;

    public void Resource(ResourceConfig config) {
        config.sensors.forEach((name, type) -> {
            sensors.add(new Sensor(name, type));
        });
        sensorSrc = config.sensorSrc;
        actorSrc = config.actorSrc;
    }
}
