package platform.service.inv.struct.grptracefile;

import java.io.*;
import java.util.List;

public class GrpTraceDaikon extends GrpTraceAbstract{
    private List<String> varNames;

    public GrpTraceDaikon(String subTraceDir) {
        super(subTraceDir);
    }

    @Override
    protected void printVarNames(String appName, int gid, int lineNumber, List<String> varNames) throws IOException {
        this.varNames = varNames;
        String fileName = grpTraceDir + appName + "-line" + lineNumber + "-grp" + gid + ".decls";
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
    protected void printValues(String appName, int gid, int lineNumber, List<String> values) throws IOException{
        String fileName = grpTraceDir + appName + "-line" + lineNumber + "-grp" + gid + ".dtrace";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        out.write("aprogram.point:::POINT\n");
        for (int i = 0; i < varNames.size(); i++) {
            out.write(varNames.get(i) + "\n" + values.get(i) + "\n1\n");
        }
        out.write("\n");
        out.close();
    }

    @Override
    public void close() {

    }
}
