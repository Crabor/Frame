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
    public void setProperty(JSONObject jo) {
        //background
        try {
            Color color = Util.parseColor(jo.getString("background"));
            label.setBackground(color);
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, jo.getString("background")));
        } catch (Exception ignored) {}

        //text
        try {
            String text = jo.getString("text");
            label.setText(text);
            logger.info(String.format("<%s,%s>.setText(%s)", type, id, text));
        } catch (Exception ignored) {}

        //font
        try {
            String[] fontSetting = Util.jsonArrayToStringArray(jo.getJSONArray("font"));
            String fontName = fontSetting[0];
            FontStyleType fontStyle = FontStyleType.fromString(fontSetting[1]);
            int fontSize = Integer.parseInt(fontSetting[2]);
            label.setFont(new Font(fontName, fontStyle.ordinal(), fontSize));
            logger.info(String.format("<%s,%s>.setFont(%s)", type, id, jo.getString("font")));
        } catch (Exception ignored) {}
    }
}
