package platform.service.inv.struct;

//基础信息结构体
public class CheckInfo {
    public String appName;//app名称
    public int iterId;//第几次迭代
    public int lineNumber;//check行号

    public int checkId;//第几次check
    public long checkTime;//check时间
    public String name;//Cancer实例的名字
    public double value;//Cancer实例对应的T实例的值

    public boolean isViolated;//是否违反了不变式

    public CheckInfo(String appName, int iterId, int lineNumber, int checkId, long checkTime, String name, double value, boolean isViolated) {
        this.appName = appName;
        this.lineNumber = lineNumber;
        this.iterId = iterId;
        this.checkId = checkId;
        this.checkTime = checkTime;
        this.name = name;
        this.value = value;
        this.isViolated = isViolated;
    }

    @Override
    public String toString() {
        return "{" +
                "appName='" + appName + '\'' +
                ", iterId=" + iterId +
                ", lineNumber=" + lineNumber +
                ", checkId=" + checkId +
                ", checkTime=" + checkTime +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", isViolated=" + isViolated +
                '}';
    }
}