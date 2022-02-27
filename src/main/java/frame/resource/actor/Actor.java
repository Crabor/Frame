package frame.resource.actor;

public class Actor {
    public Dir dir;
    public Double speed;
    public Double time;

    public Actor(Dir dir, Double speed, Double time) {
        this.dir = dir;
        this.speed = speed;
        this.time = time;
    }

    public Actor(String dir, Double speed, Double time) {
        this(Dir.valueOf(dir.toUpperCase()), speed, time);
    }
}
