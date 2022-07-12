package platform.service.inv.struct.trace;

import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.SegInfo;
import platform.util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TraceAbstract implements Trace{
    protected static String ROOT_DIR_NAME = "output/trace/";
    protected String traceDir;

    @Override
    public String toString() {
        return getClass().getSimpleName().substring(5).toLowerCase();
    }

    protected abstract void printVarNames(String appName,int lineNumber, int gid,  List<String> varNames) throws IOException;
    protected abstract void printValues(String appName, int lineNumber, int gid, List<String> values) throws IOException;

    protected TraceAbstract(String subTraceDir) {
        traceDir = ROOT_DIR_NAME + subTraceDir;
        if (!subTraceDir.matches("(.*)\\/")) {
            traceDir += "/";
        }
        File dir = new File(traceDir);
        Util.deleteDir(dir);
        dir.mkdirs();
    }

    private List<String> getVarNames(List<CheckInfo> checkInfos){
        List<String> ret = new ArrayList<>();
        int checkId = 1;
        for (CheckInfo info : checkInfos) {
            if (info.checkId == checkId) {
                ret.add(info.name);
            } else {
                break;
            }
        }
        return ret;
    }

    private List<String> getValuesByIndex(List<CheckInfo> checkInfos, int index, int valuesNum) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < valuesNum; i++) {
            CheckInfo info = checkInfos.get(index * valuesNum + i);
            ret.add(String.valueOf(info.value));
        }
        return ret;
    }

    public void printGrpTraceOverView(String appName, int lineNumber, int gid, List<Integer> trace) {
        String fileName = traceDir + appName + "-line" + lineNumber + "-overview";
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
            out.write("grp" + gid + "=" + trace + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printTrace(String appName, int lineNumber, int gid, Map<Integer, SegInfo> segs, List<Integer> trace) {
        printGrpTraceOverView(appName, lineNumber, gid, trace);
        for (Integer iter : trace) {
            SegInfo seg = segs.get(iter);
            if (seg.pCxt.contains(lineNumber)) {
                try {
                    List<CheckInfo> checkInfos = seg.checkTable.get(lineNumber);
                    List<String> varNames = getVarNames(checkInfos);
                    printVarNames(appName, lineNumber, gid, varNames);
                    int checkNum = checkInfos.size() / varNames.size();
                    for (int j = 0; j < checkNum; j++) {
                        printValues(appName, lineNumber, gid, getValuesByIndex(checkInfos, j, varNames.size()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
