package ui.component;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import ui.struct.ComponentType;
import ui.struct.FontStyleType;

import javax.swing.*;
import java.awt.*;

public class TextField extends AbstractComponent {
    JTextArea textArea;

    public TextField(ComponentType type, String id) {
        super(type, id);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setBaseComponent(textArea);
    }

    @Override
    public void setProperty(JSONObject jo) {
        //background
        try {
            Color color = Util.parseColor(jo.getString("background"));
            textArea.setBackground(color);
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, jo.getString("background")));
        } catch (Exception ignored) {}

        //text
        try {
            String text = jo.getString("text");
            textArea.setText(text);
            logger.info(String.format("<%s,%s>.setText(%s)", type, id, text));
        } catch (Exception ignored) {}

        //font
        try {
            String[] fontSetting = Util.jsonArrayToStringArray(jo.getJSONArray("font"));
            String fontName = fontSetting[0];
            FontStyleType fontStyle = FontStyleType.fromString(fontSetting[1]);
            int fontSize = Integer.parseInt(fontSetting[2]);
            textArea.setFont(new Font(fontName, fontStyle.ordinal(), fontSize));
            logger.info(String.format("<%s,%s>.setFont(%s)", type, id, jo.getString("font")));
        } catch (Exception ignored) {}

        //rows
        try {
            int rows = jo.getInteger("rows");
            textArea.setRows(rows);
            logger.info(String.format("<%s,%s>.setRows(%s)", type, id, rows));
        } catch (Exception ignored) {}

        //columns
        try {
            int columns = jo.getInteger("columns");
            textArea.setColumns(columns);
            logger.info(String.format("<%s,%s>.setColumns(%s)", type, id, columns));
        } catch (Exception ignored) {}

        //editable
        try {
            boolean editable = jo.getBoolean("editable");
            textArea.setEditable(editable);
            logger.info(String.format("<%s,%s>.setEditable(%s)", type, id, editable));
        } catch (Exception ignored) {}
    }
}
