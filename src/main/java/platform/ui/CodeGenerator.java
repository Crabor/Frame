package platform.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeGenerator {
    private static final String COMPONENT_PACKAGE = "javax.swing";
    private static final String FRAME_CLASS_NAME = "JFrame";

    public static void generate(String templateFilePath, String outputFilePath) throws IOException {
        String className = templateFilePath.substring(templateFilePath.lastIndexOf("/") + 1,
                templateFilePath.lastIndexOf("."));
        System.out.println(className);

        // 从文件读取模板配置
        String templateJson = FileUtils.readFileToString(new File(templateFilePath), "UTF-8");
        JSONObject templateObject = JSON.parseObject(templateJson);

        // 解析frame属性
        JSONObject frameObject = templateObject.getJSONObject("frame");
        String title = frameObject.getString("title");
        int width = frameObject.getIntValue("width");
        int height = frameObject.getIntValue("height");
        String layout = frameObject.getString("layout");

        // 创建JFrame对象
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("import ").append(COMPONENT_PACKAGE).append(".").append(FRAME_CLASS_NAME).append(";\n\n");
        codeBuilder.append("public class ").append(className).append(" extends JFrame {\n\n");
        codeBuilder.append("    public ").append(className).append("() {\n");
        codeBuilder.append("        setTitle(\"").append(title).append("\");\n");
        codeBuilder.append("        setSize(").append(width).append(", ").append(height).append(");\n");
        codeBuilder.append("        setLayout(new ").append(layout).append("());\n\n");

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

            // 创建组件对象
            codeBuilder.append("        ").append(type).append(" ").append(id).append(" = new ").append(type).append("();\n");
            codeBuilder.append("        ").append(id).append(".setBounds(").append(x).append(", ").append(y).append(", ")
                    .append(width).append(", ").append(height).append(");\n");
            if (componentObject.containsKey("text")) {
                String text = componentObject.getString("text");
                codeBuilder.append("        ").append(id).append(".setText(\"").append(text).append("\");\n");
            }

            // 添加到JFrame中
            codeBuilder.append("        add(").append(id).append(");\n\n");
        }

        // 结束JFrame构造函数
        codeBuilder.append("        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n");
        codeBuilder.append("    }\n");
        codeBuilder.append("}");

        // 将生成的代码输出到Java文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
        writer.write(codeBuilder.toString());
        writer.close();
    }

    public static void main(String[] args) {
        try {
            // 生成Java文件
            generate("Resources/config/platform/ui/testpage.json", "Resources/config/platform/ui/testpage.java");
            // 编译Java文件
            Process process = Runtime.getRuntime().exec("javac -d Resources/config/platform/ui/ Resources/config/platform/ui/testpage.java");
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Compilation successful.");
            } else {
                System.out.println("Compilation failed.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
