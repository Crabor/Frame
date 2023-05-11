package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;
import ui.struct.ScrollType;

import javax.swing.*;
import java.awt.*;

public class Window extends AbstractLayout {
    private JFrame frame;

    public Window(ComponentType type, String id) {
        super(type, id);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        layout = new GridBagLayout();
        panel = new JPanel(layout);
        frame.add(panel, BorderLayout.CENTER);
        setBaseComponent(frame);
    }

    @Override
    public void setTitle(String title) {
        frame.setTitle(title);
    }

   @Override
   public void setScroll(ScrollType type) {
       frame.remove(panel);
       scrollPane = new JScrollPane(panel);
       setScrollBar(type);
       frame.add(scrollPane, BorderLayout.CENTER);
   }

    public void show() {
        frame.setVisible(true);
    }
}
