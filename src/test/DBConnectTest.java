package test;

import model.DBConnect;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBConnectTest {
    private Connection con;
    private Statement st;
    private ResultSet rs;

    private DBConnect dbConnect;

    public DBConnectTest() {
        dbConnect = new DBConnect();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    //denne test tjekker om film og ide passer med det forventede - OG om rækkefølgen er rigtig.
    @Test
    public void testGetMovies(){
        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(1, "Interstellar");
        expected.put(2, "Fury");
        expected.put(3, "Antboy");
        expected.put(5, "Mockingjay");
        expected.put(6, "Dommeren");
        expected.put(7, "Bryllupskaos");
        expected.put(8, "The Interview");
        expected.put(9, "Dumb and Dumber To");
        expected.put(10, "Nightcrawler");
        expected.put(11, "Jurrasic World");

        assertEquals(expected, dbConnect.getMovies());
    }

    //denne test tjekker tidspunkterne på film nr 1 virker - 1 kan udskiftes med andre id's. Virker stadig.
    @Test
    public void testTimeStamp() throws SQLException {
        try {
            st = con.createStatement();
            String query = "SELECT * FROM shows WHERE movie_id=1 ORDER BY time ASC";
            rs = st.executeQuery(query);
            Timestamp tmstmp = new Timestamp(0);
            while(rs.next()) {
                System.out.println("Id eksisterer"); //id 4 eksisterer ikke
                assertTrue(tmstmp.getTime() < rs.getTimestamp("time").getTime());
                if(rs.getTimestamp("time").getTime() > tmstmp.getTime()) {
                    tmstmp = rs.getTimestamp("time");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

}