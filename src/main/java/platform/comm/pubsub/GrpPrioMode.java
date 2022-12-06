package platform.comm.pubsub;

public class GrpPrioMode {
    public int groupId;
    public int priorityId;
    public long mode;

    public GrpPrioMode(int groupId, int priorityId, long mode) {
        this.groupId = groupId;
        this.priorityId = priorityId;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "{" + groupId + "," + priorityId + "," + mode + "}";
    }
}
