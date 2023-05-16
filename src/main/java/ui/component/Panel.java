package ui.component;

import ui.struct.AlignType;
import ui.struct.ComponentType;
import ui.struct.ScrollType;

import javax.swing.*;
import java.awt.*;

public class Panel extends AbstractLayout {
    public Panel(ComponentType type, String id) {
        super(type, id);
        layout = new GridBagLayout();
        panel = new JPanel(layout);
        setLinkComponent(panel);
        setBaseComponent(panel);
    }

    @Override
    public void setScroll(ScrollType scrollType) {
        AbstractLayout layout = getParent();
        if (layout != null) {
            int[] position = layout.getComponentPosition(this);
            AlignType align = layout.getComponentAlign(this);
            layout.removeComponent(this, false);
            scrollPane = new JScrollPane(panel);
            setScrollBar(scrollType);
            setLinkComponent(scrollPane);
            layout.setComponent(this, position[0], position[1], position[2], position[3], align, false);
        } else {
            scrollPane = new JScrollPane(panel);
            setScrollBar(scrollType);
            setLinkComponent(scrollPane);
        }
    }

    @Override
    public String getScroll() {
        String ret;
        if (linkComponent == scrollPane) {
            boolean horizontal = scrollPane.getHorizontalScrollBarPolicy() != ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
            boolean vertical = scrollPane.getVerticalScrollBarPolicy() != ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
            if (horizontal && vertical) {
                ret = "both";
            } else if (horizontal) {
                ret = "horizontal";
            } else if (vertical) {
                ret = "vertical";
            } else {
                ret = "none";
            }
        } else {
            ret = "none";
        }
        return ret;
    }
}
