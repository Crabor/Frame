package database;

import database.struct.QueryResult;

public class test1 {
    public static void main(String[] args) {
        Database.Init(9092, "school");

        Database.Set("CREATE TABLE IF NOT EXISTS student (id INT PRIMARY KEY, name VARCHAR(255))");
        Database.Set("INSERT INTO student VALUES (1, '张三')");
        Database.Set("INSERT INTO student VALUES (2, '李四')");

        QueryResult qr = Database.Get("SELECT * FROM student");
        System.out.println(qr);
        Database.Close();
    }
}
