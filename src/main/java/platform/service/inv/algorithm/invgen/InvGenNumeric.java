package platform.service.inv.algorithm.invgen;

import com.opencsv.CSVReader;
import platform.service.inv.struct.inv.InvAbstract;
import platform.service.inv.struct.inv.InvNumeric;
import platform.util.Util;
import reactor.util.function.Tuple3;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvGenNumeric implements InvGen{
    //appname, name, linenumber, group, inv
    private Map<String, Map<String, Map<Integer, Map<Integer, InvAbstract>>>> invMap = new HashMap<>();
    //appname, name, linenumber, group, trace(list)
    private Map<String, Map<String, Map<Integer, Map<Integer, List<Double>>>>> traceMap= new HashMap<>();
    private static String ROOT_DIR_NAME = "output/grouptrace/";
    private String grpTraceDir;
    public InvGenNumeric(String subGrpTraceDir) {
        grpTraceDir = ROOT_DIR_NAME + subGrpTraceDir;
        if (!subGrpTraceDir.matches("(.*)\\/")) {
            grpTraceDir += "/";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().substring(6).toLowerCase();
    }

    @Override
    public void run(){
        File dir = new File(grpTraceDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!Util.isTraceFile(file.getName())) {
                    continue;
                }
                try {
                    CSVReader reader = new CSVReader(new FileReader(file));
                    String[] names = reader.readNext();
                    Tuple3<String, Integer, Integer> tuple3 = Util.getAppNameLineNumberGroup(file.getName());
                    if (!traceMap.containsKey(tuple3.getT1())) {
                        traceMap.put(tuple3.getT1(), new HashMap<>());
                        invMap.put(tuple3.getT1(), new HashMap<>());
                    }
                    List<List<Double>> traces = new ArrayList<>();
                    List<InvAbstract> invs = new ArrayList<>();
                    for (String name : names) {
                        if (!traceMap.get(tuple3.getT1()).containsKey(name)) {
                            traceMap.get(tuple3.getT1()).put(name, new HashMap<>());
                            invMap.get(tuple3.getT1()).put(name, new HashMap<>());
                        }
                        if (!traceMap.get(tuple3.getT1()).get(name).containsKey(tuple3.getT2())) {
                            traceMap.get(tuple3.getT1()).get(name).put(tuple3.getT2(), new HashMap<>());
                            invMap.get(tuple3.getT1()).get(name).put(tuple3.getT2(), new HashMap<>());
                        }
                        traceMap.get(tuple3.getT1()).get(name).get(tuple3.getT2()).put(tuple3.getT3(), new ArrayList<>());
                        traces.add(traceMap.get(tuple3.getT1()).get(name).get(tuple3.getT2()).get(tuple3.getT3()));
                        invMap.get(tuple3.getT1()).get(name).get(tuple3.getT2()).put(tuple3.getT3(), new InvNumeric());
                        invs.add(invMap.get(tuple3.getT1()).get(name).get(tuple3.getT2()).get(tuple3.getT3()));
                    }
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        for (int i = 0; i < nextLine.length; i++) {
                            traces.get(i).add(Double.parseDouble(nextLine[i]));
                        }
                    }
                    for (int i = 0; i < traces.size(); i++) {
                        ((InvNumeric)invs.get(i)).genInv(traces.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Map<String, Map<String, Map<Integer, Map<Integer, InvAbstract>>>> getInvMap() {
        return invMap;
    }
}
