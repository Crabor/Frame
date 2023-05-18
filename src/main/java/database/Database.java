package database;

import database.struct.QueryResult;
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
    static Connection conn = null;
    static Statement stmt = null;
    static Lock lock = new ReentrantLock();

    public static void Init(int port, String database) {
        try {
            // 加载H2数据库的驱动程序
            Class.forName("org.h2.Driver");

            // 连接到H2数据库，test如果不存在则自动创建
            conn = DriverManager.getConnection(String.format("jdbc:h2:tcp://localhost:%d/mem:%s", port, database),
                    "sa", "");

            // 创建一个Statement对象
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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

    public static QueryResult Get(String sql) {
        if (conn == null || stmt == null) {
            return null;
        }
        QueryResult result = null;
        ResultSet rs = null;
        try {
            lock.lock();
            rs = stmt.executeQuery(sql);
        } catch (SQLException ignored) {
        } finally {
            lock.unlock();
        }
        if (rs != null) {
            result = new QueryResult(rs);
        }
//        System.out.println(result);
        return result;
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
            e.printStackTrace();
            result = false;
        } finally {
            lock.unlock();
        }
        return result;
    }
}
