package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
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
        panel.setBackground(Color.WHITE);
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
            setBaseComponent(scrollPane);
            layout.setComponent(this, position[0], position[1], position[2], position[3], align, false);
        } else {
            scrollPane = new JScrollPane(panel);
            setScrollBar(scrollType);
            setBaseComponent(scrollPane);
        }
    }
}
