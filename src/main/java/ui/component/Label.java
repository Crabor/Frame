package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;
import ui.struct.FontStyleType;

import javax.swing.*;
import java.awt.*;

public class Label extends AbstractComponent {
    JLabel label;

    public Label(ComponentType type, String id) {
        super(type, id);
        label = new JLabel();
        setBaseComponent(label);
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }
}
