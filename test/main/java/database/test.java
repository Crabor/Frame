package database;

import database.struct.QueryResult;

import java.sql.ResultSet;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException, InterruptedException {
        Database.Init(9092, "platform");

        //Create tables
        Database.Set("CREATE TABLE IF NOT EXISTS NavigateTree (dir VARCHAR(255), file VARCHAR(255), PRIMARY KEY (dir, file))");
        Database.Set("CREATE TABLE IF NOT EXISTS AppTable (AppName VARCHAR(255) PRIMARY KEY, Description " +
                "VARCHAR(255), Service VARCHAR(255), Resource VARCHAR(255), Status VARCHAR(255))");
        Database.Set("CREATE TABLE IF NOT EXISTS ResourceTable (ResourceName VARCHAR(255), " +
                "Type VARCHAR(255), App VARCHAR(255), Status VARCHAR(255), Val VARCHAR(255), PRIMARY KEY " +
                "(ResourceName, Type))");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));
        Database.Set("CREATE TABLE IF NOT EXISTS ServiceTable (ServiceName VARCHAR(255) PRIMARY KEY, App VARCHAR(255))");

        //Service register
        Database.Set("INSERT INTO NavigateTree VALUES ('Service', 'Ctx')");
        Database.Set("INSERT INTO NavigateTree VALUES ('Service', 'Inv')");
        Database.Set("INSERT INTO ServiceTable VALUES ('Ctx', '')");
        Database.Set("INSERT INTO ServiceTable VALUES ('Inv', '')");

        //Yellow Car register
        Database.Set("INSERT INTO NavigateTree VALUES ('Resource', 'YellowCar')");
        Database.Set("INSERT INTO ResourceTable VALUES ('YellowCar', 'Sensor', '', 'online', '')");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));

        //App1 register
        Database.Set("INSERT INTO NavigateTree VALUES ('App', 'App1')");
        Database.Set("INSERT INTO AppTable VALUES ('App1', 'This is App1', '', '', 'online')");

        //App2 register
        Database.Set("INSERT INTO NavigateTree VALUES ('App', 'App2')");
        Database.Set("INSERT INTO AppTable VALUES ('App2', 'This is App2', '', '', 'online')");

        //GreenCar register
        Database.Set("INSERT INTO NavigateTree VALUES ('Resource', 'GreenCar')");
        Database.Set("INSERT INTO ResourceTable VALUES ('GreenCar', 'Sensor', '', 'online', '')");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));

        //App1 register YellowCar
        Database.Set("UPDATE AppTable SET Resource = 'YellowCar' WHERE AppName = 'App1'");
        Database.Set("UPDATE ResourceTable SET App = 'App1' WHERE ResourceName = 'YellowCar'");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));

        //App1、App2 register GreenCar
        Database.Set("UPDATE AppTable SET Resource = 'YellowCar、GreenCar' WHERE AppName = 'App1'");
        Database.Set("UPDATE ResourceTable SET App = 'App1' WHERE ResourceName = 'GreenCar'");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));
        Database.Set("UPDATE AppTable SET Resource = 'GreenCar' WHERE AppName = 'App2'");
        Database.Set("UPDATE ResourceTable SET App = 'App1、App2' WHERE ResourceName = 'GreenCar'");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));

        //App1 register Ctx
        Database.Set("UPDATE AppTable SET Service = 'Ctx' WHERE AppName = 'App1'");
        Database.Set("UPDATE ServiceTable SET App = 'App1' WHERE ServiceName = 'Ctx'");

        //YellowCar offline
        Database.Set("UPDATE ResourceTable SET Status = 'offline' WHERE ResourceName = 'YellowCar'");
//        System.out.println(Database.Get("SELECT * FROM ResourceTable"));

        int i = 0;
        while (true) {
            Thread.sleep(1000);
            System.out.println(Database.Get("SELECT * FROM ResourceTable"));
            Database.Set("UPDATE ResourceTable SET Val = '" + i++ + "' WHERE ResourceName = 'GreenCar'");
            //print ResourceTable
        }
//        Database.Close();
    }
}
