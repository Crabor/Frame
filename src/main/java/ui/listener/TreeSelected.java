package ui.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;
import ui.component.AbstractComponent;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class TreeSelected extends AbstractListener implements TreeSelectionListener {
    public TreeSelected(Action[] actions, AbstractComponent who) {
        super(actions, who);
    }

    public TreeSelected(Action[] actions, AbstractComponent who, boolean logFlag) {
        super(actions, who, logFlag);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (logFlag)
            logger.info("[LISTENER] [ITEM_SELECT]: " + who + " " + e.getPath().getLastPathComponent().toString());
        for (Action action : actions) {
            action.execute(logFlag);
        }
    }
}
