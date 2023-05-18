package ui.action;

import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.mvel2.MVEL;
import ui.UI;
import ui.component.AbstractComponent;
import ui.struct.AttributeType;
import ui.struct.ComponentType;
import ui.struct.FontStyleType;
import ui.struct.ScrollType;

import java.awt.*;
import java.util.Arrays;

public class AttributeChange extends AbstractAction {
    public AttributeChange(AbstractComponent who, JSONObject action) {
        super(who, action);
    }

    @Override
    public void execute(boolean logFlag) {
        ComponentType componentType = ComponentType.fromString(who.eval(action.get("component_type").toString()));
        String componentId = who.eval(action.get("component_id").toString());
        AbstractComponent component = UI.getComponent(componentType, componentId);
        AttributeType attributeType = AttributeType.fromString(who.eval(action.get("component_attribute").toString()));
        String value = null;
        switch (attributeType) {
            case SCROLL:
                ScrollType scrollType = ScrollType.fromString(who.eval(action.get("value").toString()));
                component.setScroll(scrollType);
                value = scrollType.toString();
                break;
            case BACKGROUND:
                Color color = Util.parseColor(who.eval(action.get("value").toString()));
                component.setBackground(color);
                value = Util.colorToString(color);
                break;
            case VISIBLE:
                boolean visible = Boolean.parseBoolean(who.eval(action.get("value").toString()));
                component.setVisible(visible);
                value = String.valueOf(visible);
                break;
            case TITLE:
                String title = who.eval(action.get("value").toString());
                component.setTitle(title);
                value = title;
                break;
            case TEXT:
                String text = who.eval(action.get("value").toString());
                component.setText(text);
                value = text;
                break;
            case COLUMN_WIDTH:
                int columnWidth = Integer.parseInt(who.eval(action.get("value").toString()));
                component.setColumnWidth(columnWidth);
                value = String.valueOf(columnWidth);
                break;
            case ROW_HEIGHT:
                int rowHeight = Integer.parseInt(who.eval(action.get("value").toString()));
                component.setRowHeight(rowHeight);
                value = String.valueOf(rowHeight);
                break;
            case EDITABLE:
                boolean editable = Boolean.parseBoolean(who.eval(action.get("value").toString()));
                component.setEditable(editable);
                value = String.valueOf(editable);
                break;
            case SIZE:
                String[] size = who.eval(Util.jsonArrayToStringArray(action.getJSONArray("value")));
                int width = Integer.parseInt(size[0]);
                int height = Integer.parseInt(size[1]);
                component.setSize(width, height);
                value = Arrays.toString(size);
                break;
            case FONT:
                String[] font = who.eval(Util.jsonArrayToStringArray(action.getJSONArray("value")));
                String fontName = font[0];
                FontStyleType fontStyle = FontStyleType.fromString(font[1]);
                int fontSize = Integer.parseInt(font[2]);
                component.setFont(new Font(fontName, fontStyle.ordinal(), fontSize));
                value = Arrays.toString(font);
                break;
            case COLUMN_NAMES:
                String[] columnNames = who.eval(Util.jsonArrayToStringArray(action.getJSONArray("value")));
                component.setColumnNames(columnNames);
                value = Arrays.toString(columnNames);
                break;
            case DIRS:
                String[] dirs = who.eval(Util.jsonArrayToStringArray(action.getJSONArray("value")));
                component.setDirs(dirs);
                value = Arrays.toString(dirs);
                break;
            case USER_VALS:
                String[] userVals = who.eval(Util.jsonArrayToStringArray(action.getJSONArray("value")));
                component.setUserVals(userVals);
                value = Arrays.toString(userVals);
                break;
        }
        if (logFlag)
            logger.info(String.format("[ACTION] [ATTRIBUTE_CHANGE]: %s.%s = %s", component, attributeType, value));
    }
}
