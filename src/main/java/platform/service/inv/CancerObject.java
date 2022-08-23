package platform.service.inv;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.config.Configuration;
import platform.pubsub.Publisher;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.service.inv.struct.InvState;
import platform.service.inv.struct.inv.InvAbstract;
import platform.service.inv.struct.trace.Trace;
import platform.struct.InvGenMode;
import platform.util.Util;

import java.util.*;

public class CancerObject {
    private int iterId;
    private int checkId;
    private final String appName;
    private final String name;
    private double value;

    private final List<Integer> lines = new ArrayList<>();

    //静态变量，第一维为appName，第二维为name，第三维为cancerObject
    private static final Map<String, Map<String, CancerObject>> objs = new HashMap<>();
    //第一维为行号，第二维为组号，第三维为不变式
    private final Map<Integer, Map<Integer, InvAbstract>> invMap = new HashMap<>();

    private static final Log logger = LogFactory.getLog(CancerObject.class);

    private Publisher publisher = new Publisher();

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

    public List<Integer> getLines() {
        return lines;
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
            if (group == -1 && Configuration.getCancerServerConfig().getInvGenMode() == InvGenMode.INCR) {
                if (!invs.containsKey(-1)) {
                    String invClassName = "platform.service.inv.struct.inv.Inv" +
                            Util.makeFirstCharUpperCase(Configuration.getCancerServerConfig().getInvGenType().toString().toLowerCase());
                    try {
                        InvAbstract inv = (InvAbstract) Class.forName(invClassName).newInstance();
                        inv.setMetaData(appName, lineNumber, -1, name, new ArrayList<>());
                        invs.put(-1, inv);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                InvAbstract inv = invs.get(-1);
                inv.addViolatedIter(iterId);
                outputTraceAndGenNewInv(lineNumber, group, invs, inv);
            } else if (invs.containsKey(group)) {
                InvAbstract inv = invs.get(group);
                if (inv.getState() == InvState.TRACE_COLLECT) {
                    inv.addViolatedIter(iterId);
                    outputTraceAndGenNewInv(lineNumber, group, invs, inv);
                } else if (inv.getState() == InvState.INV_GENERATING) {
                    checkState = CheckState.INV_GENERATING;
                } else if (inv.isViolated(value)) {
                    checkState = CheckState.INV_VIOLATED;
                    inv.addViolatedIter(iterId);
                    outputTraceAndGenNewInv(lineNumber, group, invs, inv);
                } else {
                    checkState = CheckState.INV_NOT_VIOLATED;
                    inv.clearViolatedIters();
                }
            }
        }
        return checkState;
    }

    private void outputTraceAndGenNewInv(int lineNumber, int group, Map<Integer, InvAbstract> invs, InvAbstract inv) {
        if (Configuration.getCancerServerConfig().getInvGenMode() == InvGenMode.INCR &&
                inv.getViolatedTrace().size() > Configuration.getCancerServerConfig().getGroupThro()) {
            String invClassName = "platform.service.inv.struct.inv.Inv" +
                    Util.makeFirstCharUpperCase(Configuration.getCancerServerConfig().getInvGenType().toString().toLowerCase());
            try {
                InvAbstract invNew = (InvAbstract) Class.forName(invClassName).newInstance();
                invNew.setMetaData(appName, lineNumber, group + 1, name, inv.getViolatedTrace(), InvState.INV_GENERATING);
                invs.put(group + 1, invNew);
            } catch (InstantiationException |
                     IllegalAccessException |
                     ClassNotFoundException e) {
                e.printStackTrace();
            }
            InvAbstract invNew = invs.get(group + 1);
            //output trace
            Trace traceOutput =  Configuration.getCancerServerConfig().getGroupTraceType();
            traceOutput.printTrace(appName, lineNumber, group + 1, CancerServer.getSegMap().get(appName), invNew.getTrace());
            logger.info("grp" + (group + 1) + "=" + invNew.getTrace());
            //gen new inv
            invNew.genInv();
            invNew.setState(InvState.INV_GENERATED);
        }
    }

    public CheckInfo check(int lineNumber, int group) {
        checkId++;
        if (!invMap.containsKey(lineNumber)) {
            invMap.put(lineNumber, new HashMap<>());
        }
        if (!lines.contains(lineNumber)) {
            lines.add(lineNumber);
        }
        if (!CancerServer.getLineMap().containsKey(appName)) {
            CancerServer.getLineMap().put(appName, new HashMap<>());
        }
        if (!CancerServer.getLineMap().get(appName).containsKey(lineNumber)) {
            CancerServer.getLineMap().get(appName).put(lineNumber, new ArrayList<>());
        }
        if (!CancerServer.getLineMap().get(appName).get(lineNumber).contains(this)) {
            CancerServer.getLineMap().get(appName).get(lineNumber).add(this);
        }
        CheckState checkState = getCheckState(lineNumber, group);
        CheckInfo checkInfo = new CheckInfo(appName, iterId, lineNumber, checkId, new Date().getTime(), name, value,
                checkState, checkState == CheckState.INV_VIOLATED ? invMap.get(lineNumber).get(group).getDiff(value) : 0);
        // pub to check channel
        publisher.publish("check", JSONObject.toJSONString(checkInfo));
        return checkInfo;
    }

    public CheckInfo check(int lineNumber) {
        int group = -1;
        if (!invMap.containsKey(lineNumber)) {
            invMap.put(lineNumber, new HashMap<>());
        }
        Map<Integer, InvAbstract> invs = invMap.get(lineNumber);
        if (!invs.isEmpty()) {
            group = Util.getMaxKey(invs);
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
