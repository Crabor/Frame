package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException {
        Database.Init(9092, "school");

        Database.Set("CREATE TABLE IF NOT EXISTS student (id INT PRIMARY KEY, name VARCHAR(255))");
        Database.Set("INSERT INTO student VALUES (1, '张三')");
        Database.Set("INSERT INTO student VALUES (2, '李四')");

        ResultSet rs = Database.Get("SELECT * FROM student");
        assert rs != null;
        System.out.println("id name");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " " + rs.getString("name"));
        }

        Database.Close();
    }
}
