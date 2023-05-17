package ui.listener;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.UI;
import ui.action.Action;
import ui.component.AbstractComponent;
import ui.component.AbstractLayout;
import ui.struct.AlignType;
import ui.struct.ComponentType;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseClick implements MouseListener {
    Action action;
    Log logger = LogFactory.getLog(MouseClick.class);

    public MouseClick(Action action) {
        this.action = action;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            logger.info("[MOUSE_CLICK]: ");
            action.execute();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
