package database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    static Connection conn = null;
    static Statement stmt = null;
    static Lock lock = new ReentrantLock();
    static int defaultFreq = 1;

    public static void Init(int port, String database) {
        try {
            // 加载H2数据库的驱动程序
            Class.forName("org.h2.Driver");

            // 连接到H2数据库，test如果不存在则自动创建
            conn = DriverManager.getConnection(String.format("jdbc:h2:tcp://localhost:%d/mem:%s", port, database),
                    "sa", "");

            // 创建一个Statement对象
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void Close() {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultFreq(int freq) {
        defaultFreq = freq;
    }

    public static int getDefaultFreq() {
        return defaultFreq;
    }

    public static ResultSet Get(String sql) {
        if (conn == null || stmt == null) {
            return null;
        }
        ResultSet rs = null;
        try {
            lock.lock();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
//            if (!(e instanceof JdbcSQLSyntaxErrorException)) {
//                e.printStackTrace();
//            }
        } finally {
            lock.unlock();
        }
        //System.out.printf("Get(%s) -> %s%n", sql, rs);
        return rs;
    }

    public static boolean Set(String sql) {
        if (conn == null || stmt == null) {
            return false;
        }
        boolean result = true;
        try {
            lock.lock();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
//            e.printStackTrace();
            result = false;
        } finally {
            lock.unlock();
        }
        //System.out.printf("Set(%s) -> %b%n", sql, result);
        return result;
    }
}
