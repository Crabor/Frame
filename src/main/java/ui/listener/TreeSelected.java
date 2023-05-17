package ui.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class TreeSelected implements TreeSelectionListener {
    Action action;
    Log logger = LogFactory.getLog(TreeSelected.class);

    public TreeSelected(Action action) {
        this.action = action;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        logger.info("[ITEM_SELECT]: " + e.getPath().getLastPathComponent().toString());
        action.execute();
    }
}
