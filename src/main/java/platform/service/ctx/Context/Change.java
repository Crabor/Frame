package platform.service.ctx.Context;

import java.util.concurrent.atomic.AtomicLong;

public class Change<T> {
    public static final char ADDITION = '+';
    public static final char DELETION = '-';
    public static AtomicLong INDEX_TAG = new AtomicLong();

    enum Change_TYPE {
        ADDITION, DELETION;
    }
    private long index;
    private char change_type;
    private String SensorName;
    private String SensorData;
    private String timestamps;

    Change(char type, String sensorName, String sensorData){
        this.change_type = type;
        SensorName = sensorName;
        SensorData = sensorData;
    }
    Change(char type, Context context){
        this.change_type = type;
        index = context.getIndex();
        SensorName = context.getSensorName();
        SensorData = String.valueOf(context.getSensorData());
        timestamps = context.getTimestamps();

    }
    public String toString(){
        return change_type + "," + index + "," + SensorName + "," + SensorData + ","+ timestamps;
    }
}
