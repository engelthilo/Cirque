package model;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DBConnect {

    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

    }

    private Connection getCon() {
        try {
            if(!con.isValid(3)) {
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return con;
    }

    public LinkedHashMap<Integer, String> getMovies() {
        LinkedHashMap<Integer, String> movies = new LinkedHashMap<Integer, String>();
        try {
            st = getCon().createStatement();

            String query = "SELECT * FROM movies ORDER BY movie_name ASC";

            rs = st.executeQuery(query);
            while(rs.next()) {
                int id = rs.getInt("id");
                String movieName = rs.getString("movie_name");
                movies.put(id, movieName);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return movies;
    }

    public LinkedHashMap<Integer, Timestamp> getMovieSchedule(int movieId) {
        LinkedHashMap<Integer, Timestamp> times = new LinkedHashMap<Integer, Timestamp>();
        try {
            st = getCon().createStatement();
            String query = "SELECT * FROM shows WHERE movie_id=" + movieId + " ORDER BY time ASC";
            rs = st.executeQuery(query);
            while(rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("time");
                times.put(id, timestamp);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return times;
    }

    public buildHolder getBuildSceneInfo(int showId) {
        buildHolder bh = new buildHolder(); //skaber en buildholder - som er det "grafiske"

        // first lets get the rest of the info needed to build the cinema
        try {
            st = getCon().createStatement();
            String query = "SELECT time, movie_name, cinema_name, columns, rows FROM shows, movies, cinemas WHERE cinemas.id = shows.cinema_id AND movies.id = shows.movie_id AND shows.id = " + showId;
            rs = st.executeQuery(query);

            while(rs.next()) {
                bh.setTime(rs.getTimestamp("time")); // gets and sets the time of a given show
                bh.setColumns(rs.getInt("columns")); // gets and sets the width of the cinema
                bh.setRows(rs.getInt("rows")); // gets and sets the height of the cinema
                bh.setMovieName(rs.getString("movie_name")); // gets and sets the moviename of the show
                bh.setCinemaName(rs.getString("cinema_name")); // gets the cinema name (sal navn)
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // next lets get all reserved seats
        try {
            st = getCon().createStatement();
            String query = "SELECT seat_x, seat_y FROM reservationlines, reservations WHERE reservationlines.reservation_id = reservations.id AND reservations.show_id = " + showId;
            rs = st.executeQuery(query);

            Boolean[][] resSeat = new Boolean[100][100];
            int i = 0;
            while(rs.next()) {
                int seat_x = rs.getInt("seat_x"); //gets the x-value of a reserved seat
                int seat_y = rs.getInt("seat_y"); // gets the y-value of a reserved seat
                resSeat[seat_x][seat_y] = true; // like resSeat[3][4] is true if reserved seat: 3:4
                i++;
            }
            bh.setReservedNumber(i); // sets the number of reserved seats into the buildholder
            bh.setResSeat(resSeat); // sets the reserved seat-array into the buildholder (object)
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        bh.setShowId(showId);
        return bh;
    }

    public Boolean insertReservation(ArrayList<String> seats, int showId, String customerName, String customerPhone) {
        try {
            st = getCon().createStatement();
            // simple insert statement where the order details with customer name and phone is inserted with a given showid
            String query = "INSERT INTO reservations (show_id, customer_name, customer_phone) VALUES ('" + showId + "', '" + customerName + "', '" + customerPhone + "')";
            st.executeUpdate(query);
            rs = st.executeQuery("select last_insert_id() as last_id from reservations"); // gets the last inserted reservation so that we can bind the seats to that reservation
            String lastid = "";
            if (rs.next()) {
                lastid = rs.getString(1); // simply gets the last id
            }

            //time to insert the seats of an order - just looping through them and inserting (binded with the latest id in reservations)
            for(String seat : seats) {
                String[] seatInfo = seat.split(":"); // here we split the string since it is like 3:3 or 9:17 so that we can get the x-value and the y-value seperated. They are now stored in an array with index 0 being the x-value and index 1 being the y-value
                query = "INSERT INTO reservationlines (reservation_id, seat_x, seat_y) VALUES ('" + lastid + "', '" + seatInfo[0] + "', '" + seatInfo[1] + "')";
                st.executeUpdate(query);
            }

            return true;

        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }

    public LinkedHashMap<Integer, String> getReservations(String phoneNumber) {
        LinkedHashMap<Integer, String> reservations = new LinkedHashMap<Integer, String>();
        try {
            st = getCon().createStatement();
            String query = "SELECT shows.id, time, movie_name, cinema_name, reservations.id, customer_name FROM reservations, cinemas, movies, shows WHERE reservations.customer_phone='" + phoneNumber + "' AND reservations.show_id = shows.id AND shows.movie_id = movies.id AND shows.cinema_id = cinemas.id";
            rs = st.executeQuery(query);
            while(rs.next()) {
                String movieName = rs.getString("movie_name");
                String customerName = rs.getString("customer_name");
                String cinemaName = rs.getString("cinema_name");
                Timestamp timestamp = rs.getTimestamp("time");
                int id = rs.getInt("reservations.id");
                String resString = movieName + "   " + new SimpleDateFormat("dd/MM HH:mm").format(timestamp);
                reservations.put(id, resString);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return reservations;
    }

}