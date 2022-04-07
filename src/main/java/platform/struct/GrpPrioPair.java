package platform.struct;

public class GrpPrioPair {
    public int groupId;
    public int priorityId;

    public GrpPrioPair(int groupId, int priorityId) {
        this.groupId = groupId;
        this.priorityId = priorityId;
    }

    @Override
    public String toString() {
        return "GrpPrioPair{" +
                "groupId=" + groupId +
                ", priorityId=" + priorityId +
                '}';
    }
}
