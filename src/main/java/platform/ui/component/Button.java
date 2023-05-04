package platform.ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import platform.ui.struct.ComponentType;

import javax.swing.*;
import java.awt.*;

public class Button extends AbstractComponent {
    JButton button;

    public Button(ComponentType type, String id) {
        super(type, id);
        button = new JButton();
        setBaseComponent(button);
    }

    @Override
    public void setProperty(JSONObject jo) {
        //background
        try {
            Color color = Util.parseColor(jo.getString("background"));
            button.setBackground(color);
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, jo.getString("background")));
        } catch (Exception ignored) {}

        //text
        try {
            String text = jo.getString("text");
            button.setText(text);
            logger.info(String.format("<%s,%s>.setText(%s)", type, id, text));
        } catch (Exception ignored) {}

        //font
        try {
            String[] font = Util.jsonArrayToStringArray(jo.getJSONArray("font"));;
            String fontName = font[0];
            int fontStyle = Integer.parseInt(font[1]);
            int fontSize = Integer.parseInt(font[2]);
            button.setFont(new Font(fontName, fontStyle, fontSize));
            logger.info(String.format("<%s,%s>.setFont(%s)", type, id, jo.getString("font")));
        } catch (Exception ignored) {}

        //TODO: add more properties
    }
}
