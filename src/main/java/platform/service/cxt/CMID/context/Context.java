package platform.service.cxt.CMID.context;

import java.util.Objects;

/**
 * Created by njucjc on 2017/10/3.
 */
public class Context {
    private int no;
    private String SensorName;
    private String id;
    private String SensorData;

    private long timestamp;

    public Context(int no, String SensorName, String id, String SensorData, long timestamp) {
        this.no = no;
        this.SensorName = SensorName;
        this.id = id;

        this.SensorData = SensorData;

        this.timestamp = timestamp;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getSensorName() {
        return SensorName;
    }

    public void setSensorName(String type) {
        this.SensorName = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensorData() {
        return SensorData;
    }

    public void setSensorData(double longitude) {
        this.SensorData = SensorData;
    }



    public String allForString() {
        return no + "," + SensorName + "," + id + ","
               + SensorData + "," +  "," + timestamp;
    }

    @Override
    public String toString() {
        return ContextParser.contextToJsonWithNo(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Context context = (Context) o;

        return Objects.equals(id, context.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
