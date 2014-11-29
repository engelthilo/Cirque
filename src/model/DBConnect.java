package model;

import java.sql.*;
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

        // first lets get all reserved seats
        try {
            st = getCon().createStatement();
            String query = "SELECT seat_x, seat_y FROM reservationlines, reservations WHERE reservationlines.reservation_id = reservations.id AND reservations.show_id = " + showId;
            rs = st.executeQuery(query);

            int[] x = new int[100];
            int[] y = new int[100];
            int i = 0;
            while(rs.next()) {
                int seat_x = rs.getInt("seat_x");
                int seat_y = rs.getInt("seat_y");
                x[seat_x] = 1; // if seat is reserved the value is 1 else its null
                y[seat_y] = 1; // if seat is reserved the value is 1 else its null
                i++;
            }
            bh.setReservedNumber(i);
            bh.setReserved_x(x); // sets the reserved seats (x-value) to the build object
            bh.setReserved_y(y); // sets the reserved seats (y-value) to the build object
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // next lets get the rest of the info needed to build the cinema
        try {
            st = getCon().createStatement();
            String query = "SELECT time, movie_name, cinema_name, columns, rows FROM shows, movies, cinemas WHERE cinemas.id = shows.cinema_id AND movies.id = shows.movie_id AND shows.id = " + showId;
            rs = st.executeQuery(query);

            while(rs.next()) {
                bh.setTime(rs.getTimestamp("time"));
                bh.setColumns(rs.getInt("columns"));
                bh.setRows(rs.getInt("rows"));
                bh.setMovieName(rs.getString("movie_name"));
                bh.setCinemaName(rs.getString("cinema_name"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return bh;
    }

}