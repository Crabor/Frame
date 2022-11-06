package platform.config;

import com.alibaba.fastjson.JSONObject;

public class ActuatorConfig {
    private final String ActuatorName;
    private boolean isAlive = false;
    private static int aliveFreq;

    public ActuatorConfig(JSONObject object) {
        this.ActuatorName = object.getString("ActuatorName");
    }

    public static int getAliveFreq() {
        return aliveFreq;
    }

    public static void setAliveFreq(int freq) {
        aliveFreq = freq;
    }

    public String getActorName() {
        return ActuatorName;
    }

    @Override
    public String toString() {
        return "ActuatorConfig{" +
                "ActuatorName='" + ActuatorName + '\'' +
                '}';
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
