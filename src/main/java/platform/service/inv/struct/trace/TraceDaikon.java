package platform.service.inv.struct.trace;

import java.io.*;
import java.util.List;

public class TraceDaikon extends TraceAbstract{
    private List<String> varNames;

    public TraceDaikon(String subTraceDir) {
        super(subTraceDir);
    }

    @Override
    protected void printVarNames(String appName, int lineNumber, int gid, List<String> varNames) throws IOException {
        this.varNames = varNames;
        String fileName = traceDir + appName + "-line" + lineNumber + "-grp" + gid + ".decls";
        File file = new File(fileName);
        if (!file.exists()) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write("DECLARE\naprogram.point:::POINT\n");
            for (String varName : varNames) {
                out.write(varName + "\ndouble\ndouble\n1\n");
            }
            out.close();
        }
    }

    @Override
    protected void printValues(String appName, int lineNumber, int gid, List<String> values) throws IOException {
        String fileName = traceDir + appName + "-line" + lineNumber + "-grp" + gid + ".dtrace";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        out.write("aprogram.point:::POINT\n");
        for (int i = 0; i < varNames.size(); i++) {
            out.write(varNames.get(i) + "\n" + values.get(i) + "\n1\n");
        }
        out.write("\n");
        out.close();
    }
}
