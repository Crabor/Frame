package platform.config;

import com.alibaba.fastjson.JSONObject;
import platform.app.struct.TimeLine;
import platform.app.struct.TimeNode;
import platform.communication.socket.Cmd;
import platform.communication.socket.PlatformUDP;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SensorConfig {
    private String sensorType;
    private String sensorName;
    private List<String> fieldNames;
    private boolean isAlive = false;
    private int aliveFreq; //定时ping
    private long MIN_VALUE_FREQ;
    private long MAX_VALUE_FREQ;
    private ValueThread valueThread = null;
//    private String IPAddress;
//    private int port;
    private final Set<AppConfig> apps = ConcurrentHashMap.newKeySet();
    private TimeLine timeLine = new TimeLine();
    private Lock timeLineLock = new ReentrantLock(false);

    public SensorConfig(JSONObject object){
        sensorName = object.getString("sensorName");
        try {
            sensorType = object.getString("sensorType");
        } catch (NullPointerException e) {
            sensorType = "String";
        }
        try {
            fieldNames = Arrays.asList(object.getString("fieldNames").split(","));
        } catch (NullPointerException e) {

        }
        try {
            aliveFreq = object.getInteger("aliveFreq");
        } catch (NullPointerException e) {
            aliveFreq = 1;
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
        timeLineLock.lock();
    }

    public SensorConfig(String sensorName, String sensorType, String fieldNames) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.fieldNames = Arrays.asList(fieldNames.split(","));
        timeLineLock.lock();
    }

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public boolean checkValueFreq(long freq) {
        return freq >= MIN_VALUE_FREQ && freq <= MAX_VALUE_FREQ;
    }

    public Lock getTimeLineLock() {
        return timeLineLock;
    }

    public String getSensorType() {
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

    public int getAliveFreq() {
        return aliveFreq;
    }

    public void setAliveFreq(int freq) {
        aliveFreq = freq;
    }

//    public String getIPAddress() {
//        return IPAddress;
//    }
//
//    public int getPort() {
//        return port;
//    }

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
                ", aliveFreq=" + aliveFreq +
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
            while (!shouldStop) {
                timeLineLock.lock();
                TimeNode p = timeLine.getHead().forwards[0];
                long timestamp = 0;
                while (p != null) {
                    try {
                        Thread.sleep(p.time - timestamp);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    timestamp = p.time;

                    Cmd cmd = new Cmd("sensor_get",
                            sensorName + " " + String.join(" ", p.appGrpIds));
                    PlatformUDP.send(cmd);

                    p = p.forwards[0];
                }
                timeLineLock.unlock();
            }
            stopped = true;
        }

        public void stopThread() {
            shouldStop = true;
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
