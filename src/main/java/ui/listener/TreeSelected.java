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

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        logger.info("[ITEM_SELECT] " + who + " : " + e.getPath().getLastPathComponent().toString());
        for (Action action : actions) {
            action.execute();
        }
    }
}
