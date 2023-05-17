package ui.component;

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
        setLinkComponent(panel);
        setBaseComponent(panel);
    }

    @Override
    public void setTitle(String title) {
        frame.setTitle(title);
    }

    @Override
    public String getTitle() {
        return frame.getTitle();
    }

    @Override
    public void setScroll(ScrollType type) {
        frame.remove(panel);
        scrollPane = new JScrollPane(panel);
        setScrollBar(type);
        frame.add(scrollPane, BorderLayout.CENTER);
        setLinkComponent(scrollPane);
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

    @Override
    public void setSize(int width, int height) {
        frame.setSize(width, height);
        // 获取屏幕的尺寸
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screenBounds = ge.getMaximumWindowBounds();
        // 计算窗口的坐标使其居中
        int x = (screenBounds.width - frame.getWidth()) / 2;
        int y = (screenBounds.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    @Override
    public String[] getSize() {
        String[] ret = new String[2];
        ret[0] = String.valueOf(frame.getWidth());
        ret[1] = String.valueOf(frame.getHeight());
        return ret;
    }

    public void show() {
        frame.setVisible(true);
    }

    public boolean isShow() {
        return frame.isVisible();
    }
}
