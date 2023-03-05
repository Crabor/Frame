package platform.testdaikon;
import daikon.Daikon;
import daikon.PptMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.List;

import static org.plumelib.util.MathPlume.pow;

public class testdaikon {
    List<String> varNames;

    protected void printVarNames(List<String> varNames) throws IOException {
        this.varNames = varNames;
        String fileName = "output/input.decls";
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

    protected void printValues(List<String> values) throws IOException{
        String fileName = "output/input.dtrace";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        out.write("aprogram.point:::POINT\n");
        for (int i = 0; i < varNames.size(); i++) {
            out.write(varNames.get(i) + "\n" + values.get(i) + "\n1\n");
        }
        out.write("\n");
        out.close();
    }

    public static void main(String[] args) {
        try {
            testdaikon test = new testdaikon();
            test.printVarNames(List.of("x", "y", "z"));

            for (int i = 0; i < 100; i++) {
                int x = 1;
                int y = i * 2;
                int z = pow(i, 2);
                test.printValues(List.of(String.valueOf(x), String.valueOf(y), String.valueOf(z)));
            }

            String outputFile = "output/output.inv.gz";
            String[] daikonArgs = new String[] {
                    "output/input.dtrace",
                    "output/input.decls",
                    "-o",
                    outputFile
            };
            daikon.Daikon.main(daikonArgs);
            System.out.println("Daikon analysis completed successfully!");
            String[] daikonArgs2 = new String[] {
                    "daikon.PrintInvariants",
                    outputFile
            };
            daikon.Daikon.main(daikonArgs2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
