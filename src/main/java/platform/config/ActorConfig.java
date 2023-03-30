package platform.config;

import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActorConfig {
    private final String actorName;
    private String actorType;
    private boolean isAlive = true;
    private final Set<AppConfig> apps = ConcurrentHashMap.newKeySet();

    public ActorConfig(JSONObject object) {
        actorName = object.getString("name");
        try {
            actorType = object.getString("valueType");
        } catch (NullPointerException e) {
            actorType = "String";
        }
    }

    public String getActorName() {
        return actorName;
    }

    public String getActorType() {
        return actorType;
    }

    public void addApp(AppConfig app) {
        this.apps.add(app);
    }

    public void removeApp(AppConfig app) {
        this.apps.remove(app);
    }

    public Set<AppConfig> getApps() {
        return apps;
    }

    public Set<String> getAppsName() {
        Set<String> ret = new HashSet<>();
        apps.forEach(config -> {
            ret.add(config.getAppName());
        });
        return ret;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public String toString() {
        return "ActorConfig{" +
                "actorName='" + actorName + '\'' +
                ", actorType='" + actorType + '\'' +
                '}';
    }
}
