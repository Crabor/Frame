package platform.struct;

public class Actor {
    double xSpeed;
    double ySpeed;
    double zSpeed;

    public Actor() {
        this(0, 0, 0);
    }

    public Actor(double xSpeed, double ySpeed, double zSpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public double getZSpeed() {
        return zSpeed;
    }

    public void setXSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public void setZSpeed(double zSpeed) {
        this.zSpeed = zSpeed;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "xSpeed=" + xSpeed +
                ", ySpeed=" + ySpeed +
                ", zSpeed=" + zSpeed +
                '}';
    }
}
