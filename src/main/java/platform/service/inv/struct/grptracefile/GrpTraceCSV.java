package platform.service.inv.struct.grptracefile;

import java.io.*;
import java.util.List;

public class GrpTraceCSV extends GrpTraceAbstract{
    public GrpTraceCSV(String subCSVDir) {
        super(subCSVDir);
    }

    @Override
    protected void printVarNames(String appName, int gid, int lineNumber, List<String> varNames) throws IOException {
        String fileName = grpTraceDir + appName + "_line" + lineNumber + "_grp" + gid + ".csv";
        File file = new File(fileName);
        if (!file.exists()) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(String.join(",", varNames) + "\n");
            out.close();
        }
    }

    @Override
    protected void printValues(String appName, int gid, int lineNumber, List<String> values) throws IOException {
        String fileName = grpTraceDir + appName + "_line" + lineNumber + "_grp" + gid + ".csv";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        out.write(String.join(",", values) + "\n");
        out.close();
    }

    @Override
    public void close() {

    }
}
