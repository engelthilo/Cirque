package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectSub extends DBConnect{
    private Connection con;
    private Statement st;
    private ResultSet rs;

<<<<<<< HEAD
=======

>>>>>>> 1d186ca1318bfcc399b840eedb7a3f7a767d2aaa
    public DBConnectSub() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/KaffeklubbenTest", "Kaffekluben2", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }
<<<<<<< HEAD
    
=======

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

>>>>>>> 1d186ca1318bfcc399b840eedb7a3f7a767d2aaa
}

