package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DatabaseExample {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 加载H2数据库的驱动程序
            Class.forName("org.h2.Driver");

            // 连接到H2数据库，test如果不存在则自动创建
            conn = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test", "sa", "");

            // 创建一个Statement对象
            stmt = conn.createStatement();

            while (true) {
                // 执行SQL查询语句
                rs = stmt.executeQuery("SELECT * FROM customers");

                // 处理查询结果
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email);
                }
                System.out.println();
                Thread.sleep(2000);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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