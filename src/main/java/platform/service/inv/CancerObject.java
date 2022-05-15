package platform.service.inv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.Inv;
import reactor.util.annotation.Nullable;

import java.util.*;

public class CancerObject {
    private int iterId;
    private int checkId;
    private final String appName;
    private final String name;
    private double value;

    //k为行号，v为该行的不变式
    private final Map<Integer, Inv> invs = new HashMap<>();

    //静态变量，第一维为appName，第二维为name，第三维为cancerObject
    private static final Map<String, Map<String, CancerObject>> objs = new HashMap<>();

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

    public CheckInfo check(int lineNumber) {
        checkId++;
        boolean isViolated =
                invs.containsKey(lineNumber) &&
                invs.get(lineNumber).isViolated(value);
        return new CheckInfo(appName, iterId, lineNumber, checkId, new Date().getTime(), name, value, isViolated);
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
