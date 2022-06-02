package platform.service.inv.struct.grptracefile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GrpTraceDIG extends GrpTraceAbstract {

    public GrpTraceDIG(String subGrpTraceDir) {
        super(subGrpTraceDir);
    }

    @Override
    public void close() {

    }

    @Override
    protected void printVarNames(String appName, int gid, int lineNumber, List<String> varNames) throws IOException {
        String fileName = grpTraceDir + appName + "_line" + lineNumber + "_grp" + gid + ".csv";
        File file = new File(fileName);
        if (!file.exists()) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write("vtrace1; I " + String.join("; I ", varNames) + "\n");
            out.close();
        }
    }

    @Override
    protected void printValues(String appName, int gid, int lineNumber, List<String> values) throws IOException {
        String fileName = grpTraceDir + appName + "_line" + lineNumber + "_grp" + gid + ".csv";
        List<String> intValues = new ArrayList<>();
        values.forEach(v -> {
            intValues.add(String.valueOf(Double.valueOf(v).intValue()));
        });
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        out.write("vtrace1; " + String.join("; ", intValues) + "\n");
        out.close();
    }
}
