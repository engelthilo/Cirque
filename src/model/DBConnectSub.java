package model;


import java.sql.*;

public class DBConnectSub extends DBConnect {
    private Connection con;
    private Statement st;
    private ResultSet rs;


    public DBConnectSub() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/KaffeklubbenTest", "Kaffekluben2", "kp8473moxa");
        st = con.createStatement();
    }


    public Connection getCon() throws SQLException {
        if(!con.isValid(30)) {
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/KaffeklubbenTest", "Kaffekluben2", "kp8473moxa");
        }

        return con;
    }

}

