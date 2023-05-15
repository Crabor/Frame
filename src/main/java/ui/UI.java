package ui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.util.Util;
import database.Database;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import ui.struct.AlignType;
import ui.struct.ComponentType;
import ui.component.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.exit;

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
            if (!id.contains(AbstractLayout.blankPrefix)) {
                logger.info("<" + type + "," + id + "> is created");
            }
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
            try {
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
            } catch (Exception ignored) {}
            layout.setBlank(0, 0, gridSize[0], gridSize[1]);
        }
    }

    public static void analyseProperty(JSONArray ja) {
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            AbstractComponent component = getComponent(jo);
            component.setProperty(jo);
        }
    }

    public static void Start(String configFile) throws IOException {

        JSONObject config = null;
        try {
            config = JSONObject.parseObject(FileUtils.readFileToString(new File(configFile), "UTF-8"));
        } catch (Exception e) {
            logger.error("ConfigFile is not found");
            exit(1);
        }

        String databaseName = "test";
        String tmp = config.getString("database_name");
        if (tmp != null) {
            databaseName = tmp;
        }
        logger.info("DatabaseName: " + databaseName);

        int databasePort = 9092;
        try {
            databasePort = config.getInteger("database_port");
        } catch (Exception ignored) {}
        logger.info("DatabasePort: " + databasePort);

        boolean gridVisible = false;
        try {
            gridVisible = config.getBoolean("grid_visible");
        } catch (Exception ignored) {}
        logger.info("GridVisible: " + gridVisible);

        String layoutFile = config.getString("layout_file");
        if (layoutFile == null) {
            logger.error("LayoutFile is not set");
            exit(1);
        }
        logger.info("LayoutFile: " + layoutFile);

        String propertyFile = config.getString("property_file");
        if (propertyFile == null) {
            logger.error("PropertyFile is not set");
            exit(1);
        }
        logger.info("PropertyFile: " + propertyFile);

        //设置grid可见性
        AbstractLayout.setGridVisible(gridVisible);

        //连接数据库
        Database.Init(databasePort, databaseName);
        logger.info("Connected to database");
        logger.info("");

        //读取layout
        JSONArray layout = null;
        try {
            layout = JSONObject.parseArray(FileUtils.readFileToString(new File(layoutFile), "UTF-8"));
        } catch (Exception e) {
            logger.error("LayoutFile is not found");
            exit(1);
        }
        logger.info("Start analyzing layout: " + layoutFile);
        analyseLayout(layout);
        logger.info("");

        //读取property
        JSONArray property = null;
        try {
            property = JSONObject.parseArray(FileUtils.readFileToString(new File(propertyFile), "UTF-8"));
        } catch (Exception e) {
            logger.error("PropertyFile is not found");
            exit(1);
        }
        logger.info("Start analyzing property: " + propertyFile);
        analyseProperty(property);
        logger.info("");

        //显示窗口
        Window main = (Window) getComponent(ComponentType.WINDOW, "main");
        main.show();
    }


    public static void main(String[] args) {
        try {
            UI.Start("Resources/config/ui/default.uc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
