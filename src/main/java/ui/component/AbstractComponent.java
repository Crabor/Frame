package ui.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import ui.UI;
import ui.action.*;
import ui.action.Action;
import ui.listener.MouseClick;
import ui.listener.TimerJob;
import ui.listener.TreeSelected;
import ui.struct.*;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractComponent {
    protected Log logger;
    protected final String id;
    protected final ComponentType type;
    protected Component linkComponent;
    protected Component baseComponent;
    protected AbstractLayout parent;
    protected ArrayList<String> userVals = new ArrayList<>();

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

    public void setProperty(JSONObject jo) {
        //userVals
        try {
            String[] userVals = eval(Util.jsonArrayToStringArray(jo.getJSONArray("user_vals")));
            setUserVals(userVals);
            logger.info(String.format("<%s,%s>.setUserVals%s", type, id, Arrays.toString(userVals)));
        } catch (Exception ignored) {}

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

        //visible
        try {
            boolean visible = Boolean.parseBoolean(eval(jo.get("visible").toString()));
            setVisible(visible);
            logger.info(String.format("<%s,%s>.setVisible(%s)", type, id, visible));
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

        //columnWidth
        try {
            int columnWidth = Integer.parseInt(eval(jo.get("column_width").toString()));
            setColumnWidth(columnWidth);
            logger.info(String.format("<%s,%s>.setColumnWidth(%s)", type, id, columnWidth));
        } catch (Exception ignored) {}

        //rowHeight
        try {
            int rowHeight = Integer.parseInt(eval(jo.get("row_height").toString()));
            setRowHeight(rowHeight);
            logger.info(String.format("<%s,%s>.setRowHeight(%s)", type, id, rowHeight));
        } catch (Exception ignored) {}

        //editable
        try {
            boolean editable = Boolean.parseBoolean(eval(jo.get("editable").toString()));
            setEditable(editable);
            logger.info(String.format("<%s,%s>.setEditable(%s)", type, id, editable));
        } catch (Exception ignored) {}

        //size
        try {
            String[] size = eval(Util.jsonArrayToStringArray(jo.getJSONArray("size")));
            int width = Integer.parseInt(size[0]);
            int height = Integer.parseInt(size[1]);
            setSize(width, height);
            logger.info(String.format("<%s,%s>.setSize%s", type, id, Arrays.toString(size)));
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

        //columnNames
        try {
            String[] columnNames = eval(Util.jsonArrayToStringArray(jo.getJSONArray("column_names")));
            setColumnNames(columnNames);
            logger.info(String.format("<%s,%s>.setColumnNames%s", type, id, Arrays.toString(columnNames)));
        } catch (Exception ignored) {}

        //dirs
        try {
            String[] dirs = eval(Util.jsonArrayToStringArray(jo.getJSONArray("dirs")));
            setDirs(dirs);
            logger.info(String.format("<%s,%s>.setDirs%s", type, id, Arrays.toString(dirs)));
        } catch (Exception ignored) {}

        //listeners
        try {
            JSONArray listeners = jo.getJSONArray("listeners");
            for (Object o : listeners) {
                JSONObject listener = (JSONObject) o;
                ListenerType listenerType = ListenerType.fromString(listener.getString("type"));
                ArrayList<ActionType> actionTypes = new ArrayList<>();
                listener.getJSONArray("actions").forEach(action -> {
                    actionTypes.add(ActionType.fromString(((JSONObject)action).getString("type")));
                });
                setListener(listener);
                logger.info(String.format("<%s,%s>.setListener(%s,%s)", type, id, listenerType, actionTypes));
            }
        } catch (Exception ignored) {}

        //content
//        try {
//            String content = jo.getString("content");
//            if (content != null) {
//                setContent(content);
//                logger.info(String.format("<%s,%s>.setContent(%s)", type, id, content));
//            }
//        } catch (Exception ignored) {}
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

    public void setUserVals(String[] userVals) {
        this.userVals = new ArrayList<>(Arrays.asList(userVals));
    }

    public String[] getUserVals() {
        return userVals.toArray(new String[0]);
    }

    public void setUserVal(int index, String val) {
        int size = userVals.size();
        if (index < size) {
            userVals.set(index, val);
        } else {
            for (int i = size; i < index; i++) {
                userVals.add("");
            }
            userVals.add(val);
        }
    }

    public String getUserVal(int index) {
        int size = userVals.size();
        String ret;
        if (index < size) {
            ret = userVals.get(index);
        } else {
            for (int i = size; i <= index; i++) {
                userVals.add("");
            }
            ret = "";
        }
        return ret;
    }

    //layout attributes
    public String[] getPosition() {
        int[] position = parent.getComponentPosition(this);
        return new String[]{String.valueOf(position[0]), String.valueOf(position[1]), String.valueOf(position[2]), String.valueOf(position[3])};
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

    public String getSelectedItem() {
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

    public String[] getSelectedPath() {
        return null;
    }

    public void setListener(JSONObject listener) {
        ListenerType listenerType = ListenerType.fromString(listener.getString("type"));
        JSONArray actionObjs = listener.getJSONArray("actions");
        Action[] actions = new Action[actionObjs.size()];
        for (int i = 0; i < actionObjs.size(); i++) {
            JSONObject actionObj = actionObjs.getJSONObject(i);
            Action action = null;
            ActionType actionType = ActionType.fromString(actionObj.getString("type"));
            switch (actionType) {
                case LAYOUT_CHANGE:
                    action = new LayoutChange(this, actionObj);
                    break;
                case DATABASE_GET:
                    action = new DatabaseGet(this, actionObj);
                    break;
                case DATABASE_SET:
                    action = new DatabaseSet(this, actionObj);
                    break;
                case ATTRIBUTE_CHANGE:
                    action = new AttributeChange(this, actionObj);
                    break;
            }
            actions[i] = action;
        }
        switch (listenerType) {
            case MOUSE_CLICK:
                baseComponent.addMouseListener(new MouseClick(actions, this));
                break;
            case TIMER:
                int sleepTime = 1000;
                try {
                    sleepTime = 1000 / listener.getInteger("freq");
                } catch (Exception ignored) {}
                Timer timer = new Timer(sleepTime, new TimerJob(actions, this, false));
                timer.start();
                break;
            case ITEM_SELECT:
                if (this instanceof Tree) {
                    ((Tree)this).setTreeSelectedListener(new TreeSelected(actions, this));
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "<" + type + "," + id + ">";
    }

    public boolean isDisplayed() {
        boolean ret;
        if (this instanceof Window) {
            ret = ((Window) this).isShow();
        } else {
            AbstractComponent parent = getParent();
            while (true) {
                if (parent == null) {
                    ret = false;
                    break;
                } else if (parent instanceof Window) {
                    ret = ((Window) parent).isShow();
                    break;
                } else {
                    parent = parent.getParent();
                }
            }
        }
        return ret;
    }

    VariableResolverFactory resolverFactory = new MapVariableResolverFactory();
    public String[] eval(String[] strs) {
        String[] ret = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            ret[i] = eval(strs[i]);
        }
        return ret;
    }

    public String eval(String str) {
//        logger.info(this + " before eval: " + str);
        String pattern = "\\$\\{[a-zA-Z0-9_]+(.[a-zA-Z0-9_]+)*}";
        ArrayList<String> vars = new ArrayList<>();
        Matcher matcher = Pattern.compile(pattern).matcher(str);
        while (matcher.find()) {
            vars.add(matcher.group());
        }
        ArrayList<String> replace = new ArrayList<>();
        boolean evaluate = true;
        for (String var : vars) {
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
            } else if (split[index].equalsIgnoreCase("systime")) {
                //HH:MM:SS
                replace.add(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                evaluate = false;
            } else if (split[index].equalsIgnoreCase("systime_ms")) {
                //HH:MM:SS:MS
                replace.add(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
                evaluate = false;
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
                    case SELECTED_ITEM:
                        replace.add(component.getSelectedItem());
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
                    case SELECTED_PATH:
                        String[] selectedPath = component.getSelectedPath();
                        replace.add(selectedPath[Integer.parseInt(split[index + 1])]);
                        break;
                    case POSITION:
                        String[] position = component.getPosition();
                        replace.add(position[Integer.parseInt(split[index + 1])]);
                        break;
                    case USER_VALS:
                        replace.add(component.getUserVal(Integer.parseInt(split[index + 1])));
                        break;
                    case CONTENT:
                        String[][] content = component.getContent();
                        replace.add(content[Integer.parseInt(split[index + 1])][Integer.parseInt(split[index + 2])]);
                        break;
                }
            }
        }
        for (int i = 0; i < vars.size(); i++) {
            str = str.replace(vars.get(i), replace.get(i));
        }
//        logger.info(this + " mid eval: " + str);
        String ret = str;
        try {
            if (evaluate) {
                ret = String.valueOf(MVEL.eval(str, resolverFactory));
            }
        } catch (Exception ignored) {}
//        logger.info(this + " after eval: " + ret);
        return ret;
    }
}
