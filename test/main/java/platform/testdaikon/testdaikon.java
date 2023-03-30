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
        //删除output/input.decls和output/input.dtrace文件和output/output.inv.gz文件
        File file = new File("output/input.decls");
        if (file.exists()) {
            file.delete();
        }
        file = new File("output/input.dtrace");
        if (file.exists()) {
            file.delete();
        }
        file = new File("output/output.inv.gz");
        if (file.exists()) {
            file.delete();
        }


        testdaikon test = new testdaikon();
        test.printVarNames(List.of("x", "y", "z", "a"));

        for (int i = 1; i <= 1000; i++) {
            int x = i;
            int y = i % 38;
            int z = x * y;
            int a =  x + y + z;
            test.printValues(List.of(String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(a)));
        }

//        EltUpperBoundFloat.dkconfig_minimal_interesting = Long.MAX_VALUE;
//        daikon.config.Configuration.getInstance().apply("daikon.inv.unary.sequence.EltUpperBoundFloat" +
//                ".maximal_interesting", String.valueOf(Long.MAX_VALUE));
//        daikon.config.Configuration.getInstance().apply("daikon.inv.unary.sequence.EltUpperBoundFloat" +
//                ".minimal_interesting", String.valueOf(Long.MIN_VALUE));

        String outputFile = "output/output.inv.gz";
        String[] daikonArgs = new String[] {
//                "--nohierarchy",
                "output/input.dtrace",
                "output/input.decls",
                "-o",
                outputFile,
                "--config",
                "Resources/configFile/platform/inv/config.txt",
        };
//        Daikon.dkconfig_calc_possible_invs = true;
        daikon.Daikon.main(daikonArgs);
        //遍历输出生成的不变式
        PptMap all_ppts = Daikon.all_ppts;
        for (daikon.PptTopLevel ppt : all_ppts.pptIterable()) {
            System.out.println(ppt.name() + " (" + ppt.num_samples() + " samples)");
            for (daikon.inv.Invariant inv : ppt.getInvariants()) {
                    System.out.println(inv.format() + " " + inv.getClass().getName() + " (" + inv.ppt.num_samples() + " " +
                            "samples)" +
                            " " + inv.getConfidence());
//                    if (inv instanceof daikon.inv.unary.UnaryInvariant) {
//                        System.out.println("\tcheck 1000: " + ((daikon.inv.unary.UnaryInvariant) inv).check(1000L, 1,
//                                1));
//                    } else if (inv instanceof daikon.inv.binary.BinaryInvariant) {
//                        System.out.println("\tcheck 1000 1001: " + ((daikon.inv.binary.BinaryInvariant) inv).check(1000L
//                                , 1001L,  1, 1));
//                    } else if (inv instanceof daikon.inv.ternary.TernaryInvariant) {
//                        System.out.println("\tcheck 1000 1001 1002: " + ((daikon.inv.ternary.TernaryInvariant) inv).check(1000L
//                                , 1001L, 1002L, 1, 1));
//                    }
            }
        }
        System.out.println("Daikon analysis completed successfully!");

//        daikon.PrintInvariants.test(new String[] {outputFile});
//        daikon.DynComp.test(new String[] {"Resources.java-examples.StackAr.DataStructures.StackArTester"});
//        daikon.Chicory.test(new String[] {"--daikon", "--comparability-file=StackArTester.decls-DynComp", "Resources.java-examples.StackAr.DataStructures.StackArTester"});

    }
}
