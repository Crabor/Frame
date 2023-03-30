package app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.struct.CheckInfo;
import common.struct.SensorData;
import common.struct.enumeration.CheckResult;
import common.struct.enumeration.SensorDataType;
import common.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvCheck {
    private Log logger = LogFactory.getLog(InvCheck.class);
    private static InvCheck instance;
    AppRemoteConnector connector;
    private Map<Integer, List<String>> checkMap;
    private Map<Integer, List<String>> monitorMap;
    private Map<Integer, List<String>> isMonitoredMap;
    private Map<Integer, Integer> iterIdMap;

    public static InvCheck getInstance() {
        if (instance == null) {
            synchronized (InvCheck.class) {
                if (instance == null) {
                    instance = new InvCheck();
                }
            }
        }
        return instance;
    }

    private InvCheck() {
        checkMap = new HashMap<>();
        monitorMap = new HashMap<>();
        isMonitoredMap = new HashMap<>();
        String fileName = "test/test/java/"
                + Thread.currentThread().getStackTrace()[4].getClassName().replace(".", "/")
                + ".java";
        //打印fileName中的内容
        String[] content = Util.readFileContent(fileName, "\n").split("\n");
        for (int i = 0; i < content.length; i++) {
            Pattern checkPattern = Pattern.compile("check\\((\\w+(,\\s*\\w+)*)\\)"); // 匹配括号中的多个单词的正则表达式
            Matcher checkMatcher = checkPattern.matcher(content[i]);
            if (checkMatcher.find()) {
                String[] params = checkMatcher.group(1).split(",\\s*"); // 将匹配到的参数字符串按逗号分隔成数组
                checkMap.put(i + 1, List.of(params));
            }

            Pattern monitorPattern = Pattern.compile("monitor\\((\\w+(,\\s*\\w+)*)\\)");
            Matcher monitorMatcher = monitorPattern.matcher(content[i]);
            if (monitorMatcher.find()) {
                String[] params = monitorMatcher.group(1).split(",\\s*");
                monitorMap.put(i + 1, List.of(params));
            }

            Pattern isMonitoredPattern = Pattern.compile("isMonitored\\((\\w+(,\\s*\\w+)*)\\)");
            Matcher isMonitoredMatcher = isMonitoredPattern.matcher(content[i]);
            if (isMonitoredMatcher.find()) {
                String[] params = isMonitoredMatcher.group(1).split(",\\s*");
                isMonitoredMap.put(i + 1, List.of(params));
            }
        }
//        System.out.println(checkMap);
//        System.out.println(monitorMap);
//        System.out.println(isMonitoredMap);
    }

//    public boolean bind(AppRemoteConnector connector) {
//        if (connector == null) {
//            logger.info("[InvCheck]: bind(connector) -> false");
//            return false;
//        }
//        this.connector = connector;
//        logger.info(String.format("[%s]: bind(connector) -> true", connector.getAppName()));
//        return true;
//    }

    public boolean monitor(Object... objs) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (connector == null) {
            logger.info(String.format("[InvCheck]: monitor%s -> %s",
                    monitorMap.get(lineNumber), false));
            return false;
        }
        List<String> params = monitorMap.get(lineNumber);
        JSONObject jo = new JSONObject(2);
        jo.put("api", "inv_monitor");
        JSONArray ja = new JSONArray();
        ja.addAll(params);
        jo.put("objs", ja);
        connector.getTCP().send(jo.toJSONString());

        boolean state = false;
        String recv = connector.getTCP().recv();
        if (recv != null) {
            JSONObject retJson = JSONObject.parseObject(recv);
            state = retJson.getBoolean("state");
        }
        logger.info(String.format("[%s]: monitor%s -> %s", connector.getAppName(), monitorMap.get(lineNumber), state));
        return state;
    }

    public boolean isMonitored(Object... objs) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (connector == null) {
            logger.info(String.format("[InvCheck]: isMonitored%s -> %s",
                    isMonitoredMap.get(lineNumber), false));
            return false;
        }
        List<String> params = isMonitoredMap.get(lineNumber);
        JSONObject jo = new JSONObject(2);
        jo.put("api", "inv_is_monitored");
        JSONArray ja = new JSONArray();
        ja.addAll(params);
        jo.put("objs", ja);
        connector.getTCP().send(jo.toJSONString());

        boolean state = false;
        String recv = connector.getTCP().recv();
        if (recv != null) {
            JSONObject retJson = JSONObject.parseObject(recv);
            state = retJson.getBoolean("state");
        }
        logger.info(String.format("[%s]: isMonitored%s -> %s", connector.getAppName(), isMonitoredMap.get(lineNumber), state));
        return state;
    }

    public boolean check(Object... objs) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (connector == null) {
            logger.info(String.format("[InvCheck]: check%s -> %s",
                    isMonitoredMap.get(lineNumber), false));
            return false;
        }

        if (!iterIdMap.containsKey(lineNumber)) {
            iterIdMap.put(lineNumber, 1);
        }
        int iterId = iterIdMap.get(lineNumber);
        iterIdMap.put(lineNumber, iterId + 1);

        List<String> params = checkMap.get(lineNumber);
        JSONObject jo = new JSONObject(2);
        jo.put("api", "inv_check");
        JSONObject joo = new JSONObject(true);
        for (int i = 0; i < params.size(); i++) {
            joo.put(params.get(i), objs[i]);
        }
        jo.put("objs", joo);
        jo.put("line_number", lineNumber);
        jo.put("check_time", System.currentTimeMillis());
        jo.put("iter_id", iterId);
        connector.getTCP().send(jo.toJSONString());

        boolean state = false;
        String recv = connector.getTCP().recv();
        if (recv != null) {
            JSONObject retJson = JSONObject.parseObject(recv);
            state = retJson.getBoolean("state");
        }
        logger.info(String.format("[%s]: check%s -> %s", connector.getAppName(), checkMap.get(lineNumber),
                state));
        return state;
    }

    public static CheckResult getResult(SensorData data) {
        if (data.getType() != SensorDataType.INV_REPORT) {
//            logger.info(String.format("[%s]: getResult(data) -> null", connector.getAppName()));
            return null;
        }
        //        logger.info(String.format("[%s]: getResult(data) -> %s", connector.getAppName(), ret));
        return CheckResult.fromString((String) data.getData("result"));
    }

    public static CheckInfo getInfo(SensorData data) {
        if (data.getType() != SensorDataType.INV_REPORT) {
//            logger.info(String.format("[%s]: getInfo(data) -> null", connector.getAppName()));
            return null;
        }
        //        logger.info(String.format("[%s]: getInfo(data) -> %s", connector.getAppName(), ret));
        return new CheckInfo(
                (String) data.getData("name"),
                (Integer) data.getData("line_number"),
                (Integer) data.getData("iter_id"),
                (Long) data.getData("check_time"),
                CheckResult.fromString((String) data.getData("result")));
    }
}


