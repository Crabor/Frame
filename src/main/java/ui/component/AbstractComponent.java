package ui.component;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.struct.ComponentType;

import java.awt.*;

public abstract class AbstractComponent {
    protected Log logger;
    protected final String id;
    protected final ComponentType type;
    protected Component baseComponent;
    protected AbstractLayout parent;

    protected AbstractComponent(ComponentType type, String id) {
        this.id = id;
        this.type = type;
        logger = LogFactory.getLog(getClass());
    }

    public String getId() {
        return id;
    }

    public ComponentType getType() {
        return type;
    }

    public Component getBaseComponent() {
        return baseComponent;
    }

    public void setBaseComponent(Component component) {
        baseComponent = component;
    }

    public AbstractLayout getParent() {
        return parent;
    }

    public void setParent(AbstractLayout parent) {
        this.parent = parent;
    }

    public abstract void setProperty(JSONObject jo);

    @Override
    public String toString() {
        return "<" + type + "," + id + ">";
    }
}
