package ui.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;
import ui.component.AbstractComponent;

public class AbstractListener {
    protected Action[] actions;
    protected AbstractComponent who;
    protected Log logger;
    protected boolean logFlag = true;

    public AbstractListener(Action[] actions, AbstractComponent who) {
        this(actions, who, true);
    }

    public AbstractListener(Action[] actions, AbstractComponent who, boolean logFlag) {
        this.actions = actions;
        this.who = who;
        this.logFlag = logFlag;
        logger = LogFactory.getLog(this.getClass());
    }
}
