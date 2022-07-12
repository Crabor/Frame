package platform.service.inv.struct.trace;

import java.io.*;
import java.util.List;

public class TraceCSV extends TraceAbstract{

    public TraceCSV(String subTraceDir) {
        super(subTraceDir);
    }

    @Override
    protected void printVarNames(String appName, int lineNumber, int gid, List<String> varNames) throws IOException {
        String fileName = traceDir + appName + "-line" + lineNumber + "-grp" + gid + ".csv";
        File file = new File(fileName);
        if (!file.exists()) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(String.join(",", varNames) + "\n");
            out.close();
        }
    }

    @Override
    protected void printValues(String appName, int lineNumber, int gid, List<String> values) throws IOException {
        String fileName = traceDir + appName + "-line" + lineNumber + "-grp" + gid + ".csv";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        out.write(String.join(",", values) + "\n");
        out.close();
    }
}
