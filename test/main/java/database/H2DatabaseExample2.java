package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DatabaseExample2 {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 加载H2数据库的驱动程序，驱动文件在Resources/config/database/h2-2.1.214.jar中
            Class.forName("org.h2.Driver");

            // 连接到H2数据库，test如果不存在则自动创建
            conn = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test", "sa", "");

            // 创建一个Statement对象
            stmt = conn.createStatement();

            // 创建表
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers(id INT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))");

            // 查询表的最大ID
            rs = stmt.executeQuery("SELECT MAX(id) FROM customers");
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1) + 1;
            }

            // 定时插入数据
            while (true) {
                System.out.println("INSERT INTO customers(id, name, email) VALUES(" + maxId + ", 'name" + maxId + "', '" + maxId + "@163.com')");
                stmt.executeUpdate(String.format("INSERT INTO customers(id, name, email) VALUES(%d, '%s', '%d@163" +
                        ".com')", maxId, "name" + maxId, maxId));
                maxId++;
                Thread.sleep(2000);
            }


//            // 执行SQL查询语句
//            rs = stmt.executeQuery("SELECT * FROM customers");
//
//            // 处理查询结果
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                String email = rs.getString("email");
//                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ResultSet对象
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // 关闭Statement对象
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // 关闭Connection对象
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}