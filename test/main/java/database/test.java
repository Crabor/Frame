package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException, InterruptedException {
        Database.Init(9092, "platform");

//        Database.Set("CREATE TABLE IF NOT EXISTS tree1 (id INT PRIMARY KEY, dir VARCHAR(255), file VARCHAR(255))");
//        Database.Set("INSERT INTO tree1 VALUES (1, 'App', 'app1')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (2, 'App', 'app2')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (3, 'App', 'app3')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (4, 'Resource', 'resource1')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (5, 'Resource', 'resource2')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (6, 'Resource', 'resource3')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (7, 'Service', 'service1')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (8, 'Service', 'service2')");
//        Thread.sleep(2000);
//        Database.Set("INSERT INTO tree1 VALUES (9, 'Service', 'service3')");
//        Thread.sleep(2000);
//
//        ResultSet rs = Database.Get("SELECT * FROM tree1");
//        assert rs != null;
//        System.out.println("id dir file");
//        while (rs.next()) {
//            System.out.println(rs.getInt("id") + " " + rs.getString("dir") + " " + rs.getString("file"));
//        }
        Database.Set("CREATE TABLE IF NOT EXISTS time (time VARCHAR(255) PRIMARY KEY)");
        while (true);
//        Database.Close();
    }
}
