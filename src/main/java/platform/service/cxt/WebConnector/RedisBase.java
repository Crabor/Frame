package platform.service.cxt.WebConnector;

import java.io.Serializable;

public class RedisBase implements Serializable {
    protected String name;
    protected String info;

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
