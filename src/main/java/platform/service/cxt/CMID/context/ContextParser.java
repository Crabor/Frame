package platform.service.cxt.CMID.context;

import platform.service.cxt.CMID.util.TimestampHelper;
import com.alibaba.fastjson.*;
/**
 * Created by njucjc on 2017/10/23.
 */
public class ContextParser {
    public static Context parseContext(int no, String pattern) {
        String [] fields = pattern.split(",");
        if (fields.length != 4) {
            System.out.println("[INFO] 数据格式错误");
            System.exit(1);
        }

        String type = null;
        String id = null;
        String SensorData = "";
        long timestamp = 0L;

        try {
            type = fields[0];
            id = fields[1];
            SensorData = fields[2];
            timestamp = Long.parseLong(fields[3]);

        } catch (NumberFormatException e) {
            System.out.println("[INFO] 数据格式错误");
            System.exit(1);
        }

        return new Context(no, type, id, SensorData, timestamp);
    }

    public static Context parseChangeFromPlatfrom (String [] elements){ // example: +, 19,GPSSensor-Left,61.1198142536155, 2022-02-10 04:08:19
        if (elements.length != 5 ) {
            System.out.println("[INFO] Change格式错误1");
            System.exit(1);
        }
        //for (int i = 0; i<elements.length; i++)
        //    System.out.println("Element " + i + " : " + elements[i]);
        Context context = null;
        try {
            context = new Context(Integer.parseInt(elements[1]),
                    elements[2],
                    elements[2], elements[3], TimestampHelper.parserDate(elements[4]).getTime());
        } catch (NumberFormatException e) {
            System.out.println("[INFO] Change格式错误2");
            System.exit(1);
        }
        return context;
    }
    public static Context parseChangeContext(String [] elements) {

        //System.out.println(elements.length);
        if (elements.length != 8 ) {
            System.out.println("[INFO] Change格式错误1");
            System.exit(1);
        }

        Context context = null;
        try {
            context = new Context(Integer.parseInt(elements[2]),
                    elements[3],
                    elements[4],
                    "0.0",
                    0);
        } catch (NumberFormatException e) {
            System.out.println("[INFO] Change格式错误2");
            System.exit(1);
        }
        return context;
    }

    public static Context jsonToContext(int no, String jsonStr) {
        JSONObject object = JSONObject.parseObject(jsonStr);
        String type = object.getString("SensorName");
        String id = object.getString("ID");
        String SensorData = object.getString("SensorData");
        long timestamp = Long.parseLong(object.getString("TimeStamp"));

        return new Context(no, type, id,  SensorData, timestamp);
    }

    public static Context jsonToContextWithNo(String jsonStr) {
        JSONObject object = JSONObject.parseObject(jsonStr);
        int no = Integer.parseInt(object.getString("No"));
        String type = object.getString("SensorName");
        String id = object.getString("ID");
        String SensorData = object.getString("SensorData");

        long timestamp = Long.parseLong(object.getString("TimeStamp"));

        return new Context(no, type, id, SensorData, timestamp);
    }
    public static String contextToJson(Context context) {
        JSONObject object = new JSONObject();
        object.put("SensorName", context.getSensorName());
        object.put("ID", context.getId());
        object.put("SensorData", context.getSensorData() + "");
        object.put("TimeStamp", context.getTimestamp() + "");
        return object.toJSONString();
    }
    public static String contextToJsonWithNo(Context context) {
        JSONObject object = new JSONObject();
        object.put("No", context.getNo() + "");
        object.put("SensorName", context.getSensorName());
        object.put("ID", context.getId());
        object.put("SensorData", context.getSensorData() + "");
        object.put("TimeStamp", context.getTimestamp() + "");
        return object.toJSONString();
    }
}
