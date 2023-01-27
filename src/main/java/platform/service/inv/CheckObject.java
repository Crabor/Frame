package platform.service.inv;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import platform.config.Configuration;
import platform.comm.pubsub.Publisher;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.CheckState;
import platform.service.inv.struct.InvGenMode;
import platform.service.inv.struct.InvState;
import platform.service.inv.struct.inv.InvAbstract;
import platform.service.inv.struct.trace.Trace;
import platform.util.Util;

import java.util.*;

public class CheckObject {
    private int iterId;
    private int checkId;
    private final String appName;
    private final String name;
    private double value;

    //private final List<Integer> lines = new ArrayList<>();

    //静态变量，第一维为appName，第二维为name，第三维为cancerObject
    private static final Map<String, Map<String, CheckObject>> objs = new HashMap<>();

    //第一维为行号，第二维为组号，第三维为不变式
    private final Map<Integer, Map<Integer, InvAbstract>> invMap = new HashMap<>();

    private static final Log logger = LogFactory.getLog(CheckObject.class);

    private Publisher publisher = new Publisher();

    private CheckObject(String appName, String name, double value) {
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

    private CheckObject(String appName, String name) {
        this(appName, name, 0);
    }

    private CheckObject(String appName, double value) {
        this(appName, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()), value);
    }

    private CheckObject(String appName) {
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

    public static boolean contains(String name) {
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        return contains(appName, name);
    }

    private static void put(String appName, CheckObject checkObject) {
        if (!objs.containsKey(appName)) {
            objs.put(appName, new HashMap<>());
        }
        objs.get(appName).put(checkObject.name, checkObject);
    }

    public static CheckObject get(String appName, String name) {
        if (!objs.containsKey(appName)) {
            objs.put(appName, new HashMap<>());
        }
        Map<String, CheckObject> appObjs = objs.get(appName);
        if (!appObjs.containsKey(name)) {
            appObjs.put(name, new CheckObject(appName, name));
        }
        return appObjs.get(name);
    }

    public static CheckObject get(String name) {
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        return get(appName, name);
    }

    public static CheckObject fromJsonObjectString(String jsonObjectString) {
        JSONObject obj = JSONObject.parseObject(jsonObjectString);
        String appName = Thread.currentThread().getStackTrace()[2].getClassName();
        String key = obj.keySet().stream().findFirst().get();
        CheckObject cobj = get(appName, key);
        cobj.setValue(obj.getDoubleValue(key));
        return cobj;
    }

    public static Map<String, Map<String, CheckObject>> getAllObjs() {
        return objs;
    }

//    public List<Integer> getLines() {
//        return lines;
//    }

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
            if (group == -1 && Configuration.getInvServerConfig().getInvGenMode() == InvGenMode.INCR) {
                if (!invs.containsKey(-1)) {
                    String invClassName = "platform.service.inv.struct.inv.Inv" +
                            Util.makeFirstCharUpperCase(Configuration.getInvServerConfig().getInvGenType().toString().toLowerCase());
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
        if (Configuration.getInvServerConfig().getInvGenMode() == InvGenMode.INCR &&
                inv.getViolatedTrace().size() > Configuration.getInvServerConfig().getGroupThro()) {
            String invClassName = "platform.service.inv.struct.inv.Inv" +
                    Util.makeFirstCharUpperCase(Configuration.getInvServerConfig().getInvGenType().toString().toLowerCase());
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
            Trace traceOutput =  Configuration.getInvServerConfig().getGroupTraceType();
            traceOutput.printTrace(appName, lineNumber, group + 1, CheckServer.getSegMap().get(appName), invNew.getTrace());
            logger.info("grp" + (group + 1) + "=" + invNew.getTrace());
            //gen new inv
            invNew.genInv();
            invNew.setState(InvState.INV_GENERATED);
        }
    }

    public CheckInfo check(int lineNumber, int group) {
        checkId++;
//        if (!lines.contains(lineNumber)) {
//            lines.add(lineNumber);
//        }
        if (!CheckServer.getLineMap().containsKey(appName)) {
            CheckServer.getLineMap().put(appName, new HashMap<>());
        }
        if (!CheckServer.getLineMap().get(appName).containsKey(lineNumber)) {
            CheckServer.getLineMap().get(appName).put(lineNumber, new ArrayList<>());
        }
        if (!CheckServer.getLineMap().get(appName).get(lineNumber).contains(this)) {
            CheckServer.getLineMap().get(appName).get(lineNumber).add(this);
        }
        CheckState checkState = getCheckState(lineNumber, group);
        CheckInfo checkInfo = new CheckInfo(appName, iterId, lineNumber, checkId, new Date().getTime(), name, value,
                checkState, checkState == CheckState.INV_VIOLATED ? invMap.get(lineNumber).get(group).getDiff(value) : 0);

        CheckServer.recordCheckInfo(checkInfo);
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

    public static Map<String, CheckInfo> check(CheckObject... args) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        Map<String, CheckInfo> checkInfos = new HashMap<>();
        for (CheckObject arg : args) {
            checkInfos.put(arg.getName(), arg.check(lineNumber));
        }
        return checkInfos;
    }

    public static Map<String, CheckInfo> check(List<CheckObject> args) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        Map<String, CheckInfo> checkInfos = new HashMap<>();
        for (CheckObject arg : args) {
            checkInfos.put(arg.getName(), arg.check(lineNumber));
        }
        return checkInfos;
    }

    public static Map<String, CheckInfo> check(String... names) {
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        Map<String, CheckInfo> checkInfos = new HashMap<>();
        for (String name : names) {
            String appName = Thread.currentThread().getStackTrace()[2].getClassName();
            CheckObject obj = get(appName, name);
            checkInfos.put(obj.getName(), obj.check(lineNumber));
        }
        return checkInfos;
    }
}
