package platform.service.inv.struct;

//基础信息结构体
public class CheckInfo {
    public String appName;//app名称
    public int iterId;//第几次迭代
    public int lineNumber;//check行号

    public int checkId;//第几次check
    public long checkTime;//check时间
    public String name;//Cancer<T>实例的名字
    public double value;//Cancer<T>实例对应的T实例的值

    public CheckInfo(String appName, int checkId, long checkTime, int iterId, int lineNumber, String name, double value) {
        this.appName = appName;
        this.lineNumber = lineNumber;
        this.iterId = iterId;
        this.checkId = checkId;
        this.checkTime = checkTime;
        this.name = name;
        this.value = value;
    }
}