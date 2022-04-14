package platform.service.cxt.Context;

import com.alibaba.fastjson.JSON;
import platform.service.cxt.CMID.context.Context;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Change<T> {
    public static final char ADDITION = '+';
    public static final char DELETION = '-';
    public static AtomicLong INDEX_TAG = new AtomicLong();

    enum Change_TYPE {
        ADDITION, DELETION;
    }
    private int  index;
    private char change_type;
    private String SensorName;
    private String SensorData;
    private String timestamps;
    private long change_index;

    Change(char type, Context context){
        this.change_type = type;
        //change_index = INDEX_TAG.incrementAndGet();
        SensorName = context.getSensorName();
        SensorData = context.getSensorData();

    }
    public String toString(){
        return change_type + "," + index + "," + SensorName + "," + SensorData + ","+ timestamps;
    }
}
