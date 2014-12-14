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

    public DBConnectSub() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/KaffeklubbenTest", "Kaffekluben2", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

}
=======

    public DBConnectSub() {
        //Så skal du bare skrive SQL databse site (altså ''mysql://mysql.itu.dk:3306/kaffeklubben'' brugernavn (kaffeklubben) og kodeord (kp8473moxa
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }
    
}
>>>>>>> refs/remotes/origin/master
