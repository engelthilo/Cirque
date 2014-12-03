package test;


import controller.Controller;
import model.DBConnect;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class codeCoveraged {

    private Connection con;
    private Statement st;
    private ResultSet rs;

    private DBConnect dbConnect;

    public codeCoveraged() {
        dbConnect = new DBConnect();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben2", "kaffeklubben2", "kp8473moxa");
                st = con.createStatement();
            } catch (Exception e) {
                System.out.println("Error:" + e);
            }
        }


    Controller controller = new Controller();

    @Test
    public void testGetMovie(){


    }

}
