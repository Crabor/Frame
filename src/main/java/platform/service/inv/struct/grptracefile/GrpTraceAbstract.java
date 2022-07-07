package platform.service.inv.struct.grptracefile;
import platform.service.inv.struct.CheckInfo;
import platform.service.inv.struct.SegInfo;
import platform.util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GrpTraceAbstract implements GrpTrace{
    protected static String ROOT_DIR_NAME = "output/grouptrace/";
    protected String grpTraceDir;

    @Override
    public String toString() {
        return getClass().getSimpleName().substring(8).toLowerCase();
    }

    protected abstract void printVarNames(String appName, int gid, int lineNumber, List<String> varNames) throws IOException;
    protected abstract void printValues(String appName, int gid, int lineNumber, List<String> values) throws IOException;

    protected GrpTraceAbstract(String subGrpTraceDir) {
        grpTraceDir = ROOT_DIR_NAME + subGrpTraceDir;
        if (!subGrpTraceDir.matches("(.*)\\/")) {
            grpTraceDir += "/";
        }
        File dir = new File(grpTraceDir);
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

    public void printGrpTraceOverView(String appName, Map<Integer, List<List<Integer>>> overview) {
        overview.forEach((lineNumber, grps) -> {
            String fileName = grpTraceDir + appName + "-line" + lineNumber + "-overview";
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
                for (List<Integer> iters : grps) {
                    out.write(iters + "\n");
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void printGrpTraces(String appName, Map<Integer, SegInfo> se5gs, List<List<Integer>> grps) {
        //String simpleAppName = Util.getSimpleName(appName);
        Map<Integer, List<List<Integer>>> overview = new HashMap<>();
        for (int i = 0; i < grps.size(); i++) {
            List<Integer> iters = grps.get(i);
            for (int iterId : iters) {
                SegInfo seg = segs.get(iterId);
                int finalI = i;
                seg.pCxt.forEach(lineNumber -> {
                    if (!overview.containsKey(lineNumber)) {
                        List<List<Integer>> l = new ArrayList<>();
                        overview.put(lineNumber, l);
                    }
                    List<List<Integer>> l = overview.get(lineNumber);
                    if (l.size() < finalI + 1) {
                        List<Integer> is = new ArrayList<>();
                        l.add(is);
                    }
                    List<Integer> is = l.get(finalI);
                    is.add(iterId);
                });
                seg.checkTable.forEach((lineNumber, checkInfos) -> {
                    try {
                        List<String> varNames = getVarNames(checkInfos);
                        printVarNames(appName, finalI, lineNumber, varNames);
                        int checkNum = checkInfos.size() / varNames.size();
                        for (int j = 0; j < checkNum; j++) {
                            printValues(appName, finalI, lineNumber, getValuesByIndex(checkInfos, j, varNames.size()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        printGrpTraceOverView(appName, overview);
    }
}
