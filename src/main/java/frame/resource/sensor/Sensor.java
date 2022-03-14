package frame.resource.sensor;

public class Sensor {
    public String name;
    public Type type;
    public Double value;

    public Sensor(String name, Type type, Double value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Sensor(String name, String type, String value) {
        this(name, Type.valueOf(type.toUpperCase()), Double.valueOf(value));
    }
}
