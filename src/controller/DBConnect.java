package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by frederik on 25/11/14.
 */
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

    public HashMap<Integer, String> getMovies() {
        HashMap<Integer, String> movies = new HashMap<Integer, String>();
        try {
            st = getCon().createStatement();
            String query = "SELECT * FROM movies";
            rs = st.executeQuery(query);
            while(rs.next()) {
                int id = rs.getInt("id");
                String movieName = rs.getString("name");
                movies.put(id, movieName);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return movies;
    }

    public ArrayList<Timestamp> getMovieSchedule(int movieId) {
        ArrayList<Timestamp> times = new ArrayList<Timestamp>();
        try {
            st = getCon().createStatement();
            String query = "SELECT * FROM shows WHERE movie_id = " + movieId;
            rs = st.executeQuery(query);
            while(rs.next()) {
                times.add(rs.getTimestamp("time"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return times;
    }

}