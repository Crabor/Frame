package platform.service.ctx.CMID.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Accuracy {
    private static final Log logger = LogFactory.getLog(Accuracy.class);
    public static void main(String[] args) {
        if(args.length == 3) {
            logger.info("开始结果分析");
            logger.info("读取oracle文件并分析");
            Set<String> groundTruth = readLogFile(args[1]);
            Set<String> myLog = readLogFile(args[0]);

            int right = 0, fault = 0;
            for(String logStr : myLog) {
                if(!groundTruth.contains(logStr)) {
                    fault++;
                }
                else {
                    right++;
                }
            }

            int miss = groundTruth.size() - right;

            logger.info("结果分析完成，结果为：");
            if(fault == 0 && miss == 0) {
                LogFileHelper.getLogger().info("Oracle验证通过", true);
            }
            else {
                LogFileHelper.getLogger().info("Oracle验证不通过", true);
            }
            LogFileHelper.getLogger().info("漏报率: " + String.format("%.2f", miss * 100.0 / groundTruth.size()) + "% (" + miss + "/" + groundTruth.size() + ")", true);
            LogFileHelper.getLogger().info("误报率: " + String.format("%.2f", fault * 100.0 / groundTruth.size()) + "% (" + fault + "/" + groundTruth.size() + ")", true);
        }
        else {
            System.out.println("Usage: java Accuracy groundTruth.log myLog.log");
        }
    }

    public static Set<String> readLogFile (String filePath) {

        Set<String> strLines = new HashSet<>();

        BufferedReader bufferedReader;
        try {
            FileReader fr = new FileReader(filePath);
            bufferedReader = new BufferedReader(fr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String [] strs = line.trim().split(" ");
                if(strs.length >= 2) {
                    if(strs[1].split("_")[0].equals("ctx")) {
                        strLines.add(line.trim());
                    }
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return strLines;
    }
}