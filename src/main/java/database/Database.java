package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Database {
//    public static void main(String[] args) {
//        try {
//            // 执行命令行程序
//            Process process = Runtime.getRuntime().exec("cmd.exe /c java -cp Resources/config/database/h2-2.1.214.jar" +
//                    " org.h2.tools.Server -ifNotExists -tcp -tcpAllowOthers -tcpPort 9092");
//
//            // 将命令行程序的输出重定向到Java程序中
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//
//            // 注册一个关闭钩子，当Java程序接收到 ctrl + c 信号时触发
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    process.destroy();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
