package platform.testdaikon;
import daikon.Daikon;
import daikon.PptMap;
import daikon.inv.unary.sequence.EltUpperBoundFloat;
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
                out.write(varName + "\nint\nint\n1\n");
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

    public static void main(String[] args) throws IOException {
        testdaikon test = new testdaikon();
        test.printVarNames(List.of("x", "y", "z", "a"));

        for (int i = 0; i < 10; i++) {
            int x = i;
            int y = i * 2;
            int z = (int) Math.pow(2 , i);
            int a =  x + y + z;
            test.printValues(List.of(String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(a)));
        }

//        EltUpperBoundFloat.dkconfig_minimal_interesting = Long.MAX_VALUE;
        daikon.config.Configuration.getInstance().apply("daikon.inv.unary.sequence.EltUpperBoundFloat" +
                ".maximal_interesting", String.valueOf(Long.MAX_VALUE));
        daikon.config.Configuration.getInstance().apply("daikon.inv.unary.sequence.EltUpperBoundFloat" +
                ".minimal_interesting", String.valueOf(Long.MIN_VALUE));

        String outputFile = "output/output.inv.gz";
        String[] daikonArgs = new String[] {
                "output/input.dtrace",
                "output/input.decls",
                "-o",
                outputFile
        };
//        Daikon.dkconfig_calc_possible_invs = true;
        daikon.Daikon.main(daikonArgs);
        System.out.println("Daikon analysis completed successfully!");

//        daikon.PrintInvariants.main(new String[] {outputFile});
//        daikon.DynComp.main(new String[] {"Resources.java-examples.StackAr.DataStructures.StackArTester"});
//        daikon.Chicory.main(new String[] {"--daikon", "--comparability-file=StackArTester.decls-DynComp", "Resources.java-examples.StackAr.DataStructures.StackArTester"});

    }
}
