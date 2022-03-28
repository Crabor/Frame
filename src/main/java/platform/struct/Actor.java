package platform.struct;

public class Actor {
    int dir;
    double speed;
    int time;

    public Actor(int dir, double speed) {
        this.dir = dir; // 0:x 1:y 2:z
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "{" +
                "\"dir\":" + dir +
                ", \"speed\":" + speed +
                '}';
    }
}
