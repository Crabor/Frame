package platform.config;

import app.struct.ValueType;
import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessageGrpIds;
import common.struct.sync.SynchronousString;
import platform.app.AppMgrThread;
import platform.app.struct.TimeLine;
import platform.app.struct.TimeNode;
import platform.communication.pubsub.Publisher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SensorConfig {
    private ValueType sensorType;
    private String sensorName;
    private List<String> fieldNames;
    private boolean isAlive = true;
    private long MIN_VALUE_FREQ;
    private long MAX_VALUE_FREQ;
    private ValueThread valueThread = null;
    private final Set<AppConfig> apps = ConcurrentHashMap.newKeySet();
    private final TimeLine timeLine = new TimeLine();

    public SensorConfig(JSONObject object){
        sensorName = object.getString("name");
        try {
            sensorType = ValueType.fromString(object.getString("valueType"));
        } catch (Exception e) {
            sensorType = ValueType.STRING;
        }
        try {
            fieldNames = object.getJSONArray("fields").toJavaList(String.class);
        } catch (NullPointerException e) {
            fieldNames = new ArrayList<>();
            fieldNames.add("default");
        }
        try {
            MIN_VALUE_FREQ = object.getLong("minValueFreq");
        } catch (NullPointerException e) {
            MIN_VALUE_FREQ = 1;
        }
        try {
            MAX_VALUE_FREQ = object.getLong("maxValueFreq");
        } catch (NullPointerException e) {
            MAX_VALUE_FREQ = 1000;
        }
    }

//    public SensorConfig(String sensorName, String sensorType, String fieldNames) {
//        this.sensorName = sensorName;
//        this.sensorType = sensorType;
//        this.fieldNames = Arrays.asList(fieldNames.split(","));
//    }

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public boolean checkValueFreq(long freq) {
        return freq >= MIN_VALUE_FREQ && freq <= MAX_VALUE_FREQ;
    }

//    public Lock getTimeLineLock() {
//        return timeLineLock;
//    }

    public ValueType getSensorType() {
        return sensorType;
    }

    public String getSensorName() {
        return sensorName;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isGetValueRunning() {
        return valueThread != null;
    }

    public void startGetValue() {
        if (valueThread != null) {
            stopGetValue();
        }
        valueThread = new ValueThread();
        valueThread.start();
    }

    public void stopGetValue() {
        if (valueThread != null) {
            valueThread.stopThread();
        }
        valueThread = null;
    }

    public Set<AppConfig> getApps() {
        return apps;
    }

    public Set<String> getAppsName() {
        Set<String> ret = new HashSet<>();
        apps.forEach(config -> {
            ret.add(config.getAppName());
        });
        return ret;
    }

    @Override
    public String toString() {
        return "SensorConfig{" +
                "sensorType='" + sensorType + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", fieldNames=" + fieldNames +
                ", MIN_VALUE_FREQ=" + MIN_VALUE_FREQ +
                ", MAX_VALUE_FREQ=" + MAX_VALUE_FREQ +
                '}';
    }

    public class ValueThread extends Thread {
        private volatile boolean shouldStop = false;
        private volatile boolean stopped = true;

        @Override
        public void run() {
            stopped = false;
            Map<String, AppConfig> appConfigMap = Configuration.getAppsConfig();
            while (!shouldStop) {
//                System.out.println(timeLine);
                List<TimeNode> nodes;
                synchronized (timeLine) {
                    if (timeLine.size() == 0) {
                        try {
                            timeLine.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
//                    TimeNode p = timeLine.getHead().forwards[0];
//                    long timestamp = 0;
//                    while (p != null) {
//                        try {
//                            Thread.sleep(p.time - timestamp);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        timestamp = p.time;
//
//                        Cmd cmd = new Cmd("sensor_get",
//                                sensorName + " " + String.join(" ", p.appNames));
//                        PlatformUDP.send(cmd);
//
//                        p = p.forwards[0];
//                    }
                    nodes = timeLine.getNodes();
                }
                long timestamp = 0;
                for (TimeNode node : nodes) {
                    try {
                        Thread.sleep(node.time - timestamp);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

//                    Cmd cmd = new Cmd("sensor_get",
//                            sensorName + " " + String.join(" ", node.appNames));
//                    PlatformUDP.send(cmd);
                    List<Integer> grpIds = new ArrayList<>();
                    for (String appName : node.appNames) {
                       grpIds.add(AppMgrThread.getGrpId(appName));
                    }
                    CmdMessageGrpIds send = new CmdMessageGrpIds("sensory_request",
                            null, grpIds);
                    for (String appName : node.appNames) {
                        Map<String, SynchronousString> requestMap = appConfigMap.get(appName).getRequestMap();
                        if (!requestMap.containsKey(sensorName)) {
                            requestMap.put(sensorName, new SynchronousString());
                        }
                        requestMap.get(sensorName).put("passiveGetSensorData");
                    }
                    Publisher.publish(sensorName + "_request", send.toString());

                    timestamp = node.time;
                }
            }
            stopped = true;
        }

        public void stopThread() {
            shouldStop = true;
            synchronized (timeLine) {
                timeLine.notify();
            }
            while (!stopped) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
