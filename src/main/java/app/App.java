package app;

import common.struct.SensorData;

public interface App {
    void getMsg(String sensorName, SensorData value);
    void setting();
}
