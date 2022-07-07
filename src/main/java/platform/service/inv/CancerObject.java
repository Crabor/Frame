package platform.service.inv;

import platform.service.cxt.Configuration;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.service.inv.struct.InvState;
import platform.service.inv.struct.inv.InvAbstract;
import platform.struct.InvGenMode;
import platform.util.Util;

import java.util.*;

public class CancerObject {
    private int iterId;
    private int checkId;
    private final String appName;
    private final String name;
    private double value;

    //静态变量，第一维为appName，第二维为name，第三维为cancerObject
    private static final Map<String, Map<String, CancerObject>> objs = new HashMap<>();
    //第一维为行号，第二维为组号，第三维为不变式
    private final Map<Integer, Map<Integer, InvAbstract>> invMap = new HashMap<>();
    //第一维为appName，第二维为lineNumber，第三维为name,第四维为cancerObject
    private final Map<String, Map<Integer, Map<String, CancerObject>>> lineMap = new HashMap<>();

    public CancerObject(String appName, String name, double value) {
        if (contains(appName, name)) {
            throw new IllegalArgumentException(name + " has already exists!\n");
        }
        this.appName = appName;
        this.name = name;
        this.value = value;
        this.iterId = 1;
        this.checkId = 0;
        put(appName, this);
    }

    public CancerObject(String appName, String name) {
        this(appName, name, 0);
    }

    public CancerObject(String appName, double value) {
        this(appName, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()), value);
    }

    public CancerObject(String appName) {  
        this(appName, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()));
    }

    public String getAppName() {
        return appName;
    }

    public int getCheckId() {
        return checkId;
    }

    public int getIterId() {
        return iterId;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static boolean contains(String appName, String name) {
        return objs.containsKey(appName) && objs.get(appName).containsKey(name);
    }

    public static void put(String appName, CancerObject cancerObject) {
        if (!objs.containsKey(appName)) {
            objs.put(appName, new HashMap<>());
        }
        objs.get(appName).put(cancerObject.name, cancerObject);
    }

    public static CancerObject get(String appName, String name) {
        if (!objs.containsKey(appName)) {
            objs.put(appName, new HashMap<>());
        }
        Map<String, CancerObject> appObjs = objs.get(appName);
        if (!appObjs.containsKey(name)) {
            appObjs.put(name, new CancerObject(appName, name));
        }
        return appObjs.get(name);
    }

    public static Map<String, Map<String, CancerObject>> getAllObjs() {
        return objs;
    }

    public Map<String, Map<Integer, Map<String, CancerObject>>> getLineMap() {
        return lineMap;
    }

    public Map<Integer, Map<Integer, InvAbstract>> getInvMap() {
        return invMap;
    }

    public static void iterEntry(String appName, int iterId) {
        if (objs.containsKey(appName)) {
            objs.get(appName).forEach((k, v) -> {
                v.iterId = iterId;
                v.checkId = 0;
            });
        }
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }

    private CheckState getCheckState(int lineNumber, int group) {
        CheckState checkState = CheckState.TRACE_COLLECT;
        if (invMap.containsKey(lineNumber)) {
            Map<Integer, InvAbstract> invs = invMap.get(lineNumber);
            if (invs.containsKey(group)) {
                InvAbstract inv = invs.get(group);
                if (inv.getState() == InvState.TRACE_COLLECT) {
                    inv.addViolatedIter(iterId);
                } else if (inv.getState() == InvState.INV_GENERATING) {
                    checkState = CheckState.INV_GENERATING;
                } else if (inv.isViolated(value)) {
                    checkState = CheckState.INV_VIOLATED;
                    inv.addViolatedIter(iterId);
                    if (Configuration.getCancerServerConfig().getInvGenMode() == InvGenMode.INCR &&
                            inv.getViolatedIters().size() > Configuration.getCancerServerConfig().getGroupThro()) {
                        inv.setState(InvState.INV_GENERATING);
                        //output trace

                        //gen new inv

                        inv.setState(InvState.INV_GENERATED);
                    }
                } else {
                    checkState = CheckState.INV_NOT_VIOLATED;
                    inv.clearViolatedIters();
                }
            }
        }
        return checkState;
    }

    public CheckInfo check(int lineNumber, int group) {
        checkId++;
        if (!lineMap.containsKey(appName)) {
            lineMap.put(appName, new HashMap<>());
        }
        if (!lineMap.get(appName).containsKey(lineNumber)) {
            lineMap.get(appName).put(lineNumber, new HashMap<>());
        }
        if (!lineMap.get(appName).get(lineNumber).containsKey(name)) {
            lineMap.get(appName).get(lineNumber).put(name, this);
        }
        return new CheckInfo(appName, iterId, lineNumber, checkId, new Date().getTime(), name, value, getCheckState(lineNumber, group));
    }

    public CheckInfo check(int lineNumber) {
        int group = -1;
        if (invMap.containsKey(lineNumber)) {
            Map<Integer, InvAbstract> invs = invMap.get(lineNumber);
            if (!invs.isEmpty()) {
                group = Util.getMaxKey(invs);
            }
        }

        return check(lineNumber, group);
    }

    public CheckInfo check() {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        return check(lineNumber);
    }

    public static CheckInfo[] check(CancerObject... args) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        CheckInfo[] checkInfos = new CheckInfo[args.length];
        for (int i = 0; i < args.length; i++) {
            checkInfos[i] = args[i].check(lineNumber);
        }
        return checkInfos;
    }

    public static CheckInfo[] check(List<CancerObject> args) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        CheckInfo[] checkInfos = new CheckInfo[args.size()];
        for (int i = 0; i < args.size(); i++) {
            checkInfos[i] = args.get(i).check(lineNumber);
        }
        return checkInfos;
    }

    public static CheckInfo[] check(String... names) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        CheckInfo[] checkInfos = new CheckInfo[names.length];
        for (int i = 0; i < names.length; i++) {
            String appName = Thread.currentThread().getStackTrace()[2].getClassName();
            CancerObject obj = get(appName, names[i]);
            checkInfos[i] = obj.check(lineNumber);
        }
        return checkInfos;
    }
}
