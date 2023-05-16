package ui.action;

import com.alibaba.fastjson.JSONObject;
import ui.component.AbstractComponent;

public abstract class AbstractAction implements Action {
    protected AbstractComponent who;
    protected JSONObject action;

    public AbstractAction(AbstractComponent who, JSONObject action) {
        this.who = who;
        this.action = action;
    }
}
