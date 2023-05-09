package ui;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final String COMPONENT_PACKAGE = "javax.swing";
    private static final String FRAME_CLASS_NAME = "JFrame";
    private static final String TEXTFIELD_CLASS_NAME = "JTextField";
    private static final String BUTTON_CLASS_NAME = "JButton";

    public static void main(String[] args) throws IOException {
        // 读取模板文件
        String templateJson = FileUtils.readFileToString(new File("Resources/config/platform/ui/testpage.json"), "UTF-8");
        JSONObject templateObject = JSON.parseObject(templateJson);

        // 解析frame属性
        JSONObject frameObject = templateObject.getJSONObject("frame");
        String title = frameObject.getString("title");
        int width = frameObject.getIntValue("width");
        int height = frameObject.getIntValue("height");
        String layout = frameObject.getString("layout");

        // 创建JFrame对象并设置属性
        JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setLayout(getLayout(layout));

        // 解析components属性
        JSONArray componentsArray = templateObject.getJSONArray("components");
        for (int i = 0; i < componentsArray.size(); i++) {
            JSONObject componentObject = componentsArray.getJSONObject(i);
            String type = componentObject.getString("type");
            String id = componentObject.getString("id");
            int x = componentObject.getIntValue("x");
            int y = componentObject.getIntValue("y");
            width = componentObject.getIntValue("width");
            height = componentObject.getIntValue("height");
            String text = componentObject.getString("text");

            // 创建组件对象并设置属性
            JComponent component = getComponent(type);
            component.setBounds(x, y, width, height);
            if (component instanceof JTextComponent) {
                ((JTextComponent) component).setText(text);
            } else if (component instanceof AbstractButton) {
                ((AbstractButton) component).setText(text);
            }

            // 添加到JFrame中
            frame.add(component);
        }

        // 显示窗口
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static LayoutManager getLayout(String layoutName) {
        switch (layoutName.toLowerCase()) {
            case "flowlayout":
                return new FlowLayout();
            case "borderlayout":
                return new BorderLayout();
            case "gridlayout":
                return new GridLayout();
            default:
                return new FlowLayout();
        }
    }

    private static JComponent getComponent(String componentName) {
        try {
            Class<?> clazz = Class.forName(COMPONENT_PACKAGE + "." + componentName);
            return (JComponent) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
