package platform.ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import platform.ui.struct.ComponentType;
import platform.ui.struct.ScrollType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Window extends AbstractLayout {
    private JFrame frame;

    public Window(ComponentType type, String id) {
        super(type, id);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        layout = new GridBagLayout();
        panel = new JPanel(layout);
        frame.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void setProperty(JSONObject jo) {
        //title
        try {
            String title = jo.getString("title");
            frame.setTitle(title);
            logger.info(String.format("<%s,%s>.setTitle(%s)", type, id, title));
        } catch (Exception ignored) {}

        //size
        int width = 800;
        int height = 600;
        try {
            width = jo.getInteger("width");
            height = jo.getInteger("height");
            logger.info(String.format("<%s,%s>.setSize(%d,%d)", type, id, width, height));
        } catch (Exception ignored) {}
        frame.setSize(width, height);

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
            frame.remove(panel);
            scrollPane = new JScrollPane(panel);
            setScrollBar(scrollType);
            frame.add(scrollPane, BorderLayout.CENTER);
            logger.info(String.format("<%s,%s>.setScrollBar(%s)", type, id, scrollType));
        } catch (Exception ignored) {}
    }

    public void show() {
        frame.setVisible(true);
    }
}
