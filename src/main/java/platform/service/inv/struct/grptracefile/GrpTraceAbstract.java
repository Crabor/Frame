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
        deleteDir(dir);
        dir.mkdirs();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
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

    public void printGrpTraceOverView(String appName, Map<Integer, Map<Integer, List<Integer>>> overview) {
        overview.forEach((lineNumber, grps) -> {
            String fileName = grpTraceDir + appName + "_line" + lineNumber + "_overview";
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
                for (Map.Entry<Integer, List<Integer>> entry : grps.entrySet()) {
                    Integer grp = entry.getKey();
                    List<Integer> iters = entry.getValue();
                    out.write("grp" + grp + " = " + iters + "\n");
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void printGrpTraces(String appName, Map<Integer, SegInfo> segs, Map<Integer, List<Integer>> grps) {
        //String simpleAppName = Util.getSimpleName(appName);
        Map<Integer, Map<Integer, List<Integer>>> overview = new HashMap<>();
        grps.forEach((gid, iters) -> {
            for (int iterId : iters) {
                SegInfo seg = segs.get(iterId);
                seg.pCxt.forEach(lineNumber -> {
                    if (!overview.containsKey(lineNumber)) {
                        Map<Integer, List<Integer>> l = new HashMap<>();
                        overview.put(lineNumber, l);
                    }
                    Map<Integer, List<Integer>> l = overview.get(lineNumber);
                    if (!l.containsKey(gid)) {
                        List<Integer> is = new ArrayList<>();
                        l.put(gid, is);
                    }
                    List<Integer> is = l.get(gid);
                    is.add(iterId);
                });
                seg.checkTable.forEach((lineNumber, checkInfos) -> {
                    try {
                        List<String> varNames = getVarNames(checkInfos);
                        printVarNames(appName, gid, lineNumber, varNames);
                        int checkNum = checkInfos.size() / varNames.size();
                        for (int i = 0; i < checkNum; i++) {
                            printValues(appName, gid, lineNumber, getValuesByIndex(checkInfos, i, varNames.size()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        printGrpTraceOverView(appName, overview);
    }
}
