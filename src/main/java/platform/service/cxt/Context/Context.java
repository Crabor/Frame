package platform.service.cxt.Context;

import com.alibaba.fastjson.JSON;

public class Context<T> {
    String SensorName;
    T SensorData;
    String timestamps;
    int Index;

    public Context(String sensorName, T SensorData, String timestamps){
        this.SensorName = "pat_"+sensorName;
        this.SensorData = SensorData;
        this.timestamps = timestamps;
    }
    public Context(int index, String sensorName, T SensorData, String timestamps){
        Index = index;
        this.SensorName = "pat_"+sensorName;
        this.SensorData = SensorData;
        this.timestamps = timestamps;
    }

    public int getIndex() {
        return Index;
    }

    public String getSensorName() {
        return SensorName;
    }

    public void setSensorName(String sensorName) {
        SensorName = sensorName;
    }

    public T getSensorData() {
        return SensorData;
    }

    public void setSensorData(T sensorData) {
        SensorData = sensorData;
    }

    public void setTimestamps(String timestamps) {
        this.timestamps = timestamps;
    }

    public String getTimestamps() {
        return timestamps;
    }

    public String toString (){
        return "Timestamps = " + timestamps
                + ", SensorName = " + SensorName
                + ", SensorData = " + SensorData.toString()
                + ", Index = " + Index;
    }
    public String toJSON(){
        return JSON.toJSONString(this);
    }
}
