package test;
import controller.Controller;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.DBConnect;
import model.buildHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import model.DBConnect;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
public class DBConnectTest {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private String lastid;
    private String lastid1;
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
    public void testGetMovies() {
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
            while (rs.next()) {
                System.out.println("Id eksisterer"); //id 4 eksisterer ikke
                assertTrue(tmstmp.getTime() < rs.getTimestamp("time").getTime());
                if (rs.getTimestamp("time").getTime() > tmstmp.getTime()) {
                    tmstmp = rs.getTimestamp("time");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    @Test
    public void testReservedSetColor() {
        DBConnect dbcon = new DBConnect();
        buildHolder bh = dbcon.getBuildSceneInfo(109);
        Boolean[][] resSeat = bh.getResSeat();
        for (int i = 1; i < 31; i++) {
            for (int j = 1; j < 21; j++) {
                double width = (879 - 8 * bh.getColumns() - 8) / bh.getColumns();
                double height = (521 - 8 * bh.getRows() - 8) / bh.getRows();
                final Rectangle r = new Rectangle(width, height);
                int x = i;
                int y = j;
                if (resSeat[i][j] != null) {
                    if (resSeat[i][j]) {
                        r.setFill(Color.web("#E53935"));
                    }
                } else {
                    r.setFill(Color.web("#43A047"));
                }
                if (i == 15 && j == 9) {
                    assertTrue(r.getFill().toString().equals("0xe53935ff"));
                }
            }
        }
    }

    //tester om reservationer der bliver lavet, gemmes i databasen
    @Test
    public void testInsertReservation() {
        lastid = "";
        try {
            st = con.createStatement();
            String query = "INSERT INTO reservations (show_id, customer_name, customer_phone) VALUES (109, 'Amanda', '26802103')";
            st.executeUpdate(query);
            rs = st.executeQuery("select last_insert_id() as last_id from reservations");
            if (rs.next()) {
                lastid = rs.getString(1);
            }
            query = "INSERT INTO reservationlines (reservation_id, seat_x, seat_y) VALUES ('" + lastid + "', 1, 2)";
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            rs = st.executeQuery("SELECT reservations.id, seat_x, seat_y FROM reservations, reservationlines WHERE reservationlines.reservation_id = reservations.id AND show_id = '120' AND customer_name = 'Amanda' AND customer_phone = '26802103'");
            while (rs.next()) {
                assertEquals(rs.getString("id"), (lastid));
                assertEquals(rs.getInt("seat_x"), 1);
                assertEquals(rs.getInt("seat_y"), 2);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    //sletter den reservation vi har lavet ovenfor.
    @After
    public void deleteReservation() {
        try {
            st = con.createStatement();
            String query = "DELETE FROM reservations WHERE id = '" + lastid + "'";
            st.executeUpdate(query);
            query = "DELETE FROM reservationlines WHERE reservation_id = '" + lastid + "'";
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    //Denne test tester om der kommer en liste ud når man har reserveret for et bestemt nummer.
    @Test
    public void testInputReservation() {
        LinkedHashMap<Integer, String> reservations = dbConnect.getReservations("11223344");
        assertTrue(reservations.isEmpty());
        lastid1 = "";
        try {
            st = con.createStatement();
            String query = "INSERT INTO reservations (show_id, customer_name, customer_phone) VALUES (109, 'Markus', '11223344')";
            st.executeUpdate(query);
            rs = st.executeQuery("select last_insert_id() as last_id from reservations");
            if (rs.next()) {
                lastid1 = rs.getString(1);
            }
            query = "INSERT INTO reservationlines (reservation_id, seat_x, seat_y) VALUES ('" + lastid1 + "', 2, 2)";
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        System.out.println(lastid1);
        reservations = dbConnect.getReservations("11223344");
        assertTrue(reservations.size() == 1);
//3. test om der kommer liste ud ved enter - for det nummer - hvis ikke fail
    }

    @After
    public void deleteInputReservation() {
        try {
            st = con.createStatement();
            String query = "DELETE FROM reservations WHERE id = '" + lastid1 + "'";
            st.executeUpdate(query);
            query = "DELETE FROM reservationlines WHERE reservation_id = '" + lastid + "'";
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
