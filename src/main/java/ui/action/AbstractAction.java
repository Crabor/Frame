package ui.action;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.component.AbstractComponent;

public abstract class AbstractAction implements Action {
    protected AbstractComponent who;
    protected JSONObject action;
    protected static final Log logger = LogFactory.getLog(AbstractAction.class);

    public AbstractAction(AbstractComponent who, JSONObject action) {
        this.who = who;
        this.action = action;
    }
}
