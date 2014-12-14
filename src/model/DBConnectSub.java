package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectSub extends DBConnect{
    private Connection con;
    private Statement st;
    private ResultSet rs;


    public DBConnectSub() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/KaffeklubbenTest", "Kaffekluben2", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    public Connection getCon(){
        try {
            if(!con.isValid(30)) {
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/KaffeklubbenTest", "Kaffekluben2", "kp8473moxa");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return con;
    }

}

