package platform.service.cxt.WebConnector;

public class RedisCtxCustom extends RedisBase{
    private int all_num;
    private int problematic_num;
    private int clean_num;
    private double clean_rate;
    private int buffersize;

    public RedisCtxCustom(String name, String info){
        this.name = name;
        this.info = info;
        buffersize = 0;
    }
    RedisCtxCustom(String name, String info, int all_num, int problematic_num){
        this.name = name;
        this.info = info;
        this.all_num = all_num;
        this.problematic_num = problematic_num;
        this.clean_num = all_num - problematic_num;
        this.clean_rate =  (double) clean_num / (double) all_num;
    }

    public void updateStatistics (int all_num, int problematic_num, int buffersize){
        this.all_num = all_num;
        this.problematic_num = problematic_num;
        this.buffersize = buffersize;
    }

    public int getAll_num() {
        return all_num;
    }

    public void setAll_num(int all_num) {
        this.all_num = all_num;
    }

    public int getProblematic_num() {
        return problematic_num;
    }

    public void setProblematic_num(int problematic_num) {
        this.problematic_num = problematic_num;
    }

    public int getClean_num() {
        return clean_num;
    }

    public void setClean_num(int clean_num) {
        this.clean_num = clean_num;
    }

    public double getClean_rate() {
        return clean_rate;
    }

    public void setClean_rate(double clean_rate) {
        this.clean_rate = clean_rate;
    }

    public void setProssingRate(int all_num, int problematic_num){
        this.all_num = all_num;
        this.problematic_num = problematic_num;
        this.clean_num = all_num - problematic_num;
        this.clean_rate = (double) clean_num / (double) all_num;
    }

    @Override
    public String toString() {
        return "RedisCtxCustom{" +
                "name=" + name +
                ", info=" + info +
                ", all_num=" + all_num +
                ", problematic_num=" + problematic_num +
                ", clean_num=" + clean_num +
                ", clean_rate=" + clean_rate +
                '}';
    }
    public static void main(String[] args){
        RedisCtxCustom ctxCustom = new RedisCtxCustom("ctx0", "context for GPS", 10000, 2300);
        System.out.println(ctxCustom);
    }
}
