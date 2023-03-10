package platform.testdaikon;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class creatcsv {
    public static void main(String[] args) {
        //生成一个csv样本文件
        String csvFilePath = "output/output.csv";
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFilePath, true)));
            bw.write("x,y,z,a");
            bw.newLine();
            for (int i = 1; i < 100; i++) {
                int x = i;
                int y = i * 2;
                int z = x * y;
                int a =  x + y + z;
                bw.write(x + "," + y + "," + z + "," + a);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
