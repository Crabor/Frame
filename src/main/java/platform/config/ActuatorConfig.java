package platform.config;

import com.alibaba.fastjson.JSONObject;

public class ActuatorConfig {
    private final String ActuatorName;
    private boolean isAlive = false;
    private int aliveFreq;

    public ActuatorConfig(JSONObject object) {
        ActuatorName = object.getString("ActuatorName");
        try {
            aliveFreq = object.getInteger("aliveFreq");
        } catch (NullPointerException e) {
            aliveFreq = 1;
        }
    }

    public int getAliveFreq() {
        return aliveFreq;
    }

    public void setAliveFreq(int freq) {
        aliveFreq = freq;
    }

    public String getActorName() {
        return ActuatorName;
    }

    @Override
    public String toString() {
        return "ActuatorConfig{" +
                "ActuatorName='" + ActuatorName + '\'' +
                ", aliveFreq=" + aliveFreq +
                '}';
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
