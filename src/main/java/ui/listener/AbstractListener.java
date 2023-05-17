package ui.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;
import ui.component.AbstractComponent;

public class AbstractListener {
    protected Action[] actions;
    protected AbstractComponent who;
    protected Log logger;

    public AbstractListener(Action[] actions, AbstractComponent who) {
        this.actions = actions;
        this.who = who;
        logger = LogFactory.getLog(this.getClass());
    }
}
