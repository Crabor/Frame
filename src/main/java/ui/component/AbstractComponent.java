package ui.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import tk.pratanumandal.expr4j.ExpressionEvaluator;
import ui.UI;
import ui.action.Action;
import ui.action.DatabaseGet;
import ui.action.LayoutChange;
import ui.listener.MouseClick;
import ui.listener.TimerJob;
import ui.struct.*;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractComponent {
    protected Log logger;
    protected final String id;
    protected final ComponentType type;
    protected Component linkComponent;
    protected Component baseComponent;
    protected AbstractLayout parent;

    protected AbstractComponent(ComponentType type, String id) {
        this.id = id;
        this.type = type;
        logger = LogFactory.getLog(getClass());
    }

    public String getId() {
        return id;
    }

    public ComponentType getType() {
        return type;
    }

    public Component getLinkComponent() {
        return linkComponent;
    }

    public void setLinkComponent(Component component) {
        linkComponent = component;
    }

    public Component getBaseComponent() {
        return baseComponent;
    }

    public void setBaseComponent(Component component) {
        baseComponent = component;
    }

    public AbstractLayout getParent() {
        return parent;
    }

    public void setParent(AbstractLayout parent) {
        this.parent = parent;
    }

    public static ExpressionEvaluator expr = new ExpressionEvaluator();
    public void setProperty(JSONObject jo) {
        //scroll
        try {
            ScrollType scroll = ScrollType.fromString(eval(jo.get("scroll").toString()));
            setScroll(scroll);
            logger.info(String.format("<%s,%s>.setScroll(%s)", type, id, scroll));
        } catch (Exception ignored) {}

        //background
        try {
            Color color = Util.parseColor(eval(jo.get("background").toString()));
            setBackground(color);
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, Util.colorToString(color)));
        } catch (Exception ignored) {}

        //font
        try {
            String[] font = eval(Util.jsonArrayToStringArray(jo.getJSONArray("font")));
            String fontName = font[0];
            FontStyleType fontStyle = FontStyleType.fromString(font[1]);
            int fontSize = Integer.parseInt(font[2]);
            setFont(new Font(fontName, fontStyle.ordinal(), fontSize));
            logger.info(String.format("<%s,%s>.setFont%s", type, id, Arrays.toString(font)));
        } catch (Exception ignored) {}

        //visible
        try {
            boolean visible = Boolean.parseBoolean(eval(jo.get("visible").toString()));
            setVisible(visible);
            logger.info(String.format("<%s,%s>.setVisible(%s)", type, id, visible));
        } catch (Exception ignored) {}

        //size
        try {
            String[] size = eval(Util.jsonArrayToStringArray(jo.getJSONArray("size")));
            int width = Integer.parseInt(size[0]);
            int height = Integer.parseInt(size[1]);
            setSize(width, height);
            logger.info(String.format("<%s,%s>.setSize%s", type, id, Arrays.toString(size)));
        } catch (Exception ignored) {}

        //title
        try {
            String title = eval(jo.get("title").toString());
            setTitle(title);
            logger.info(String.format("<%s,%s>.setTitle(%s)", type, id, title));
        } catch (Exception ignored) {}

        //text
        try {
            String text = eval(jo.get("text").toString());
            if (text != null) {
                setText(text);
                logger.info(String.format("<%s,%s>.setText(%s)", type, id, text));
            }
        } catch (Exception ignored) {}

        //columnNames
        try {
            String[] columnNames = eval(Util.jsonArrayToStringArray(jo.getJSONArray("column_names")));
            setColumnNames(columnNames);
            logger.info(String.format("<%s,%s>.setColumnNames%s", type, id, Arrays.toString(columnNames)));
        } catch (Exception ignored) {}

        //columnWidth
        try {
            int columnWidth = (int)expr.evaluate(eval(jo.get("column_width").toString()));
            setColumnWidth(columnWidth);
            logger.info(String.format("<%s,%s>.setColumnWidth(%s)", type, id, columnWidth));
        } catch (Exception ignored) {}

        //rowHeight
        try {
            int rowHeight = (int)expr.evaluate(eval(jo.get("row_height").toString()));
            setRowHeight(rowHeight);
            logger.info(String.format("<%s,%s>.setRowHeight(%s)", type, id, rowHeight));
        } catch (Exception ignored) {}

        //editable
        try {
            boolean editable = Boolean.parseBoolean(eval(jo.get("editable").toString()));
            setEditable(editable);
            logger.info(String.format("<%s,%s>.setEditable(%s)", type, id, editable));
        } catch (Exception ignored) {}

        //content
//        try {
//            String content = jo.getString("content");
//            if (content != null) {
//                setContent(content);
//                logger.info(String.format("<%s,%s>.setContent(%s)", type, id, content));
//            }
//        } catch (Exception ignored) {}

        //dirs
        try {
            String[] dirs = eval(Util.jsonArrayToStringArray(jo.getJSONArray("dirs")));
            setDirs(dirs);
            logger.info(String.format("<%s,%s>.setDirs(%s)", type, id, Arrays.toString(dirs)));
        } catch (Exception ignored) {}

        //listeners
        try {
            JSONArray listeners = jo.getJSONArray("listeners");
            for (Object o : listeners) {
                JSONObject listener = (JSONObject) o;
                ListenerType listenerType = ListenerType.fromString(listener.getString("type"));
                JSONObject action = listener.getJSONObject("action");
                String actionType = action.getString("type");
                setListener(listener);
                logger.info(String.format("<%s,%s>.setListener(%s,%s)", type, id, listenerType, actionType));
            }
        } catch (Exception ignored) {}
    }

    public void setBackground(Color color) {
        baseComponent.setBackground(color);
    }

    public String getBackground() {
        return Util.colorToString(baseComponent.getBackground());
    }

    public void setFont(Font font) {
        baseComponent.setFont(font);
    }

    public String[] getFont() {
        Font font = baseComponent.getFont();
        return new String[]{font.getName(), FontStyleType.fromInt(font.getStyle()).toString(), String.valueOf(font.getSize())};
    }

    public void setVisible(boolean visible) {
        baseComponent.setVisible(visible);
    }

    public String getVisible() {
        return String.valueOf(baseComponent.isVisible());
    }

    public void setSize(int width, int height) {
        baseComponent.setSize(width, height);
    }

    public String[] getSize() {
        return new String[]{String.valueOf(baseComponent.getWidth()), String.valueOf(baseComponent.getHeight())};
    }

    public void setListener(JSONObject listener) {
        ListenerType listenerType = ListenerType.fromString(listener.getString("type"));
        JSONObject actionObj = listener.getJSONObject("action");
        Action action = null;
        ActionType actionType = ActionType.fromString(actionObj.getString("type"));
        switch (actionType) {
            case LAYOUT_CHANGE:
                action = new LayoutChange(this, actionObj);
                break;
            case DATABASE_GET:
                action = new DatabaseGet(this, actionObj);
                break;
        }
        switch (listenerType) {
            case MOUSE_CLICK:
                baseComponent.addMouseListener(new MouseClick(action));
                break;
            case TIMER:
                int sleepTime = 1000 / listener.getInteger("freq");
                Timer timer = new Timer(sleepTime, new TimerJob(action));
                timer.start();
                break;
        }
    }

    public void setScroll(ScrollType type) {}

    public String getScroll() {
        return null;
    }

    public void setTitle(String title) {}

    public String getTitle() {
        return null;
    }

    public void setText(String text) {}

    public String getText() {
        return null;
    }

    public void setColumnNames(String[] columnNames) {}

    public String[] getColumnNames() {
        return null;
    }

    public void setColumnWidth(int columnWidth) {}

    public String getColumnWidth() {
        return null;
    }

    public void setRowHeight(int rowHeight) {}

    public String getRowHeight() {
        return null;
    }

    public void setEditable(boolean editable) {}

    public String getEditable() {
        return null;
    }

    public void setContent(ResultSet content) {}

    public String[][] getContent() {
        return null;
    }

    public void setDirs(String[] dirs) {}

    public String[] getDirs() {
        return null;
    }

    //layout attributes
    public String[] getPosition() {
        int[] position = parent.getComponentPosition(this);
        return new String[]{String.valueOf(position[0]), String.valueOf(position[1]), String.valueOf(position[2]), String.valueOf(position[3])};
    }

    @Override
    public String toString() {
        return "<" + type + "," + id + ">";
    }

    public String[] eval(String[] strs) {
        String[] ret = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            ret[i] = eval(strs[i]);
        }
        return ret;
    }

    public String eval(String str) {
        String pattern = "\\$\\{[a-zA-Z0-9_]+(.[a-zA-Z0-9_]+)*}";
        ArrayList<String> vars = new ArrayList<>();
        Matcher matcher = Pattern.compile(pattern).matcher(str);
        while (matcher.find()) {
            vars.add(matcher.group());
        }
        ArrayList<String> replace = new ArrayList<>();
        vars.forEach(var -> {
            String[] split = var.substring(2, var.length() - 1).split("\\.");
            AbstractComponent component = null;
            int index = 0;
            try {
                ComponentType type = ComponentType.fromString(split[0]);
                component = UI.getComponent(type, split[1]);
                index = 2;
            } catch (Exception e) {
                component = this;
            }
            if (split[index].equalsIgnoreCase("type")) {
                replace.add(component.type.toString());
            } else if (split[index].equalsIgnoreCase("id")) {
                replace.add(component.id);
            } else {
                AttributeType attributeType = AttributeType.fromString(split[index]);
                switch (attributeType) {
                    case SCROLL:
                        replace.add(component.getScroll());
                        break;
                    case BACKGROUND:
                        replace.add(component.getBackground());
                        break;
                    case VISIBLE:
                        replace.add(component.getVisible());
                        break;
                    case TITLE:
                        replace.add(component.getTitle());
                        break;
                    case TEXT:
                        replace.add(component.getText());
                        break;
                    case COLUMN_WIDTH:
                        replace.add(component.getColumnWidth());
                        break;
                    case ROW_HEIGHT:
                        replace.add(component.getRowHeight());
                        break;
                    case EDITABLE:
                        replace.add(component.getEditable());
                        break;
                    case SIZE:
                        String[] size = component.getSize();
                        replace.add(size[Integer.parseInt(split[index + 1])]);
                        break;
                    case FONT:
                        String[] font = component.getFont();
                        replace.add(font[Integer.parseInt(split[index + 1])]);
                        break;
                    case COLUMN_NAMES:
                        String[] columnNames = component.getColumnNames();
                        replace.add(columnNames[Integer.parseInt(split[index + 1])]);
                        break;
                    case DIRS:
                        String[] dirs = component.getDirs();
                        replace.add(dirs[Integer.parseInt(split[index + 1])]);
                        break;
                    case POSITION:
                        String[] position = component.getPosition();
                        replace.add(position[Integer.parseInt(split[index + 1])]);
                        break;
                    case CONTENT:
                        String[][] content = component.getContent();
                        replace.add(content[Integer.parseInt(split[index + 1])][Integer.parseInt(split[index + 2])]);
                        break;
                }
            }
        });
        for (int i = 0; i < vars.size(); i++) {
            str = str.replace(vars.get(i), replace.get(i));
        }
        return str;
    }
}
