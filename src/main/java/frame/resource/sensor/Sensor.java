package frame.resource.sensor;

public class Sensor {
    public String name;
    public Type type;
    public Double value;

    public Sensor(String name, Type type) {
        this.name = name;
        this.type = type;
        this.value = 0.0;
    }

    public Sensor(String name, String type) {
        this(name, Type.valueOf(type.toUpperCase()));
    }
}
