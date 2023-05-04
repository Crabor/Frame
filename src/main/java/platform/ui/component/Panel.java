package platform.ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import platform.ui.struct.AlignType;
import platform.ui.struct.ComponentType;
import platform.ui.struct.ScrollType;

import javax.swing.*;
import java.awt.*;

public class Panel extends AbstractLayout {
    public Panel(ComponentType type, String id) {
        super(type, id);
        layout = new GridBagLayout();
        panel = new JPanel(layout);
        setBaseComponent(panel);
    }

    @Override
    public void setProperty(JSONObject jo) {
        //background
        Color color = Color.WHITE;
        try {
            color = Util.parseColor(jo.getString("background"));
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, jo.getString("background")));
        } catch (Exception ignored) {}
        panel.setBackground(color);

        //scroll
        try {
            ScrollType scrollType = ScrollType.fromString(jo.getString("scroll"));
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
            logger.info(String.format("<%s,%s>.setScrollBar(%s)", type, id, scrollType));
        } catch (Exception ignored) {}
    }
}
