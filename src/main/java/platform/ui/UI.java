package platform.ui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import platform.ui.component.*;
import platform.ui.struct.AlignType;
import platform.ui.struct.ComponentType;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UI {
    private static final Log logger = LogFactory.getLog(UI.class);
    private static Map<ComponentType, Map<String, AbstractComponent>> componentsMap;

    static {
        PropertyConfigurator.configure("Resources/config/log/log4jUI.properties");
        componentsMap = new ConcurrentHashMap<>();
        for(ComponentType type : ComponentType.values()) {
            componentsMap.put(type, new ConcurrentHashMap<>());
        }
    }

    public static AbstractComponent getComponent(JSONObject jo) {
        String id = jo.getString("id");
        ComponentType type = ComponentType.fromString(jo.getString("type"));
        return getComponent(type, id);
    }

    public static AbstractComponent getComponent(ComponentType type, String id) {
        Map<String, AbstractComponent> components = componentsMap.get(type);
        AbstractComponent ret = components.get(id);
        if (ret == null) {
            logger.info("<" + type + "," + id + "> is created");
            ret = createComponent(type, id);
            components.put(id, ret);
        }
        return ret;
    }

    private static AbstractComponent createComponent(ComponentType type, String id) {
        AbstractComponent ret = null;
        if (type == ComponentType.WINDOW) {
            ret = new Window(type, id);
        } else if (type == ComponentType.PANEL) {
            ret = new Panel(type, id);
        } else if (type == ComponentType.LABEL) {
            ret = new Label(type, id);
        } else if (type == ComponentType.TEXTFIELD) {
            ret = new TextField(type, id);
        } else if (type == ComponentType.BUTTON) {
            ret = new Button(type, id);
        } else if (type == ComponentType.CHECKBOX) {
            ret = new CheckBox(type, id);
        } else if (type == ComponentType.COMBOBOX) {
            ret = new ComboBox(type, id);
        } else if (type == ComponentType.LIST) {
            ret = new List(type, id);
        } else if (type == ComponentType.TABLE) {
            ret = new Table(type, id);
        } else if (type == ComponentType.TREE) {
            ret = new Tree(type, id);
        } else if (type == ComponentType.BARCHART) {
            ret = new BarChart(type, id);
        } else if (type == ComponentType.PIECHART) {
            ret = new PieChart(type, id);
        } else if (type == ComponentType.LINECHART) {
            ret = new LineChart(type, id);
        }
        return ret;
    }

    public static void analyseLayout(JSONArray ja) {
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            AbstractLayout layout = (AbstractLayout) getComponent(jo);
            int[] gridSize = Util.jsonArrayToIntArray(jo.getJSONArray("size"));
            layout.setGridSize(gridSize[0], gridSize[1]);
            JSONArray components = jo.getJSONArray("components");
            for (Object o1 : components) {
                JSONObject jo1 = (JSONObject) o1;
                AbstractComponent component = getComponent(jo1);
                int[] position = Util.jsonArrayToIntArray(jo1.getJSONArray("position"));
                AlignType align = AlignType.CENTER;
                try {
                    align = AlignType.fromString(jo1.getString("align"));
                } catch (Exception ignored) {}
                layout.setComponent(component, position[0], position[1], position[2], position[3], align);
            }
            layout.paintBlank();
        }
    }

    public static void analyseProperty(JSONArray ja) {
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            AbstractComponent component = getComponent(jo);
            component.setProperty(jo);
        }
    }

    public static void Start(String layoutFile, String propertyFile) throws IOException {
        //读取配置文件
        JSONArray layout = JSONObject.parseArray(FileUtils.readFileToString(new File(layoutFile), "UTF-8"));
        JSONArray property = JSONObject.parseArray(FileUtils.readFileToString(new File(propertyFile), "UTF-8"));
        logger.info("Start analyzing layout: " + layoutFile);
        analyseLayout(layout);
        logger.info("");
        logger.info("Start analyzing property: " + propertyFile);
        analyseProperty(property);
        logger.info("");

        //显示窗口
        Window main = (Window) getComponent(ComponentType.WINDOW, "main");
        main.show();
    }


    public static void main(String[] args) {
        try {
            UI.Start("Resources/config/platform/ui/default.layout", "Resources/config/platform/ui/default.property");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
