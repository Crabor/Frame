package ui.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;
import ui.struct.ListenerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends AbstractComponent {
    JButton button;

    public Button(ComponentType type, String id) {
        super(type, id);
        button = new JButton();
        setBaseComponent(button);
    }

    @Override
    public void setText(String text) {
        button.setText(text);
    }
}
