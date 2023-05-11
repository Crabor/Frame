package ui.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;
import ui.action.DatabaseGet;
import ui.action.LayoutChange;
import ui.listener.MouseClick;
import ui.listener.TimerJob;
import ui.struct.*;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractComponent {
    protected Log logger;
    protected final String id;
    protected final ComponentType type;
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

    public void setProperty(JSONObject jo) {
        //scroll
        try {
            ScrollType scroll = ScrollType.fromString(jo.getString("scroll"));
            setScroll(scroll);
            logger.info(String.format("<%s,%s>.setScroll(%s)", type, id, scroll));
        } catch (Exception ignored) {}

        //background
        try {
            Color color = Util.parseColor(jo.getString("background"));
            setBackground(color);
            logger.info(String.format("<%s,%s>.setBackground(%s)", type, id, jo.getString("background")));
        } catch (Exception ignored) {}

        //font
        try {
            String[] font = Util.jsonArrayToStringArray(jo.getJSONArray("font"));;
            String fontName = font[0];
            FontStyleType fontStyle = FontStyleType.fromString(font[1]);
            int fontSize = Integer.parseInt(font[2]);
            setFont(new Font(fontName, fontStyle.ordinal(), fontSize));
            logger.info(String.format("<%s,%s>.setFont%s", type, id, jo.getString("font")));
        } catch (Exception ignored) {}

        //visible
        try {
            boolean visible = jo.getBoolean("visible");
            setVisible(visible);
            logger.info(String.format("<%s,%s>.setVisible(%s)", type, id, visible));
        } catch (Exception ignored) {}

        //size
        try {
            String[] size = Util.jsonArrayToStringArray(jo.getJSONArray("size"));
            int width = Integer.parseInt(size[0]);
            int height = Integer.parseInt(size[1]);
            setSize(width, height);
            logger.info(String.format("<%s,%s>.setSize%s", type, id, jo.getJSONArray("size")));
        } catch (Exception ignored) {}

        //title
        try {
            String title = jo.getString("title");
            if (title != null) {
                setTitle(title);
                logger.info(String.format("<%s,%s>.setTitle(%s)", type, id, title));
            }
        } catch (Exception ignored) {}

        //text
        try {
            String text = jo.getString("text");
            if (text != null) {
                setText(text);
                logger.info(String.format("<%s,%s>.setText(%s)", type, id, text));
            }
        } catch (Exception ignored) {}

        //columnNames
        try {
            String[] columnNames = Util.jsonArrayToStringArray(jo.getJSONArray("column_names"));
            setColumnNames(columnNames);
            logger.info(String.format("<%s,%s>.setColumnNames%s", type, id, jo.getJSONArray("column_names")));
        } catch (Exception ignored) {}

        //columnWidth
        try {
            int columnWidth = jo.getInteger("column_width");
            setColumnWidth(columnWidth);
            logger.info(String.format("<%s,%s>.setColumnWidth(%s)", type, id, columnWidth));
        } catch (Exception ignored) {}

        //rowHeight
        try {
            int rowHeight = jo.getInteger("row_height");
            setRowHeight(rowHeight);
            logger.info(String.format("<%s,%s>.setRowHeight(%s)", type, id, rowHeight));
        } catch (Exception ignored) {}

        //editable
        try {
            boolean editable = jo.getBoolean("editable");
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
            String[] dirs = Util.jsonArrayToStringArray(jo.getJSONArray("dirs"));
            setDirs(dirs);
            logger.info(String.format("<%s,%s>.setDirs(%s)", type, id, jo.getJSONArray("dirs")));
        } catch (Exception ignored) {}

        //listeners
        try {
            JSONArray listeners = jo.getJSONArray("listeners");
            for (int i = 0; i < listeners.size(); i++) {
                ListenerType listenerType = ListenerType.fromString(listeners.getJSONObject(i).getString("type"));
                JSONObject action = listeners.getJSONObject(i).getJSONObject("action");
                String actionType = action.getString("type");
                setListener(listenerType, action);
                logger.info(String.format("<%s,%s>.setListener(%s,%s)", type, id, listenerType, actionType));
            }
        } catch (Exception ignored) {}
    }

    public void setBackground(Color color) {
        baseComponent.setBackground(color);
    }

    public void setFont(Font font) {
        baseComponent.setFont(font);
    }

    public void setVisible(boolean visible) {
        baseComponent.setVisible(visible);
    }

    public void setSize(int width, int height) {
        baseComponent.setSize(width, height);
    }

    public void setListener(ListenerType listenerType, JSONObject actionObj) {
        Action action = null;
        ActionType actionType = ActionType.fromString(actionObj.getString("type"));
        switch (actionType) {
            case LAYOUT_CHANGE:
                action = new LayoutChange(actionObj);
                break;
            case DATABASE_GET:
                action = new DatabaseGet(actionObj);
                break;
        }
        switch (listenerType) {
            case MOUSE_CLICK:
                baseComponent.addMouseListener(new MouseClick(action));
                break;
            case TIMER:
                int sleepTime = 1000 / actionObj.getInteger("freq");
                Timer timer = new Timer(sleepTime, new TimerJob(action));
                timer.start();
                break;
        }
    }

    public void setScroll(ScrollType type) {}

    public void setTitle(String title) {}

    public void setText(String text) {}

    public void setColumnNames(String[] columnNames) {}

    public void setColumnWidth(int columnWidth) {}

    public void setRowHeight(int rowHeight) {}

    public void setEditable(boolean editable) {}

    public void setContent(String content) {}

    public void setDirs(String[] dirs) {}

    @Override
    public String toString() {
        return "<" + type + "," + id + ">";
    }
}
