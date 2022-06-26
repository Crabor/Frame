package platform.service.inv.struct.inv;

import java.util.List;

public abstract class InvAbstract implements Inv {
    String appName;
    int lineNumber;
    int group;
    List<Integer> iters;
}
