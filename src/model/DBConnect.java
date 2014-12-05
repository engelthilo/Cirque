package model;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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
    /**
     * Input: None
     * Method: Gets the current connection. If it doesn't exists/is invalid a new one is created.
     * Returns: A valid connection
     */
    private Connection getCon() {
        try {
            if(!con.isValid(30)) {
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return con;
    }

    /**
     * Input: None
     * Method: Gets all the movies that exists in the database
     * Returns: Linkedhashmap with the movie id(int) as key and the movie name(string) as value
     */
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

    /**
     * Input: (int) movieId
     * Method: Gets times from every show(forestilling) a given movie(movieid) has. Sorted by time ascending
     * Returns: Linkedhashmap with the show id(int) as key and the time(timestamp) as value
     */
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

    /**
     * Input: (int) int with the showid
     * Method: Gets information about the given show (showid) and sets it into an object that holds information like time of the show, the width of the cinema, the height of the cinema, the movie name, the cinema name, it also returns a boolean multidimensional array with the reserved seats like [2][5] = true
     * Returns: buildHolder with all the information about the show. Can be accessed like bh.getTime(), bh.getColumns, bh.getRows, bh.getMovieName, bh.getCinemaName, bh.getResSeat <- this is the reserved seats in multidimensional array
     */
    public buildHolder getBuildSceneInfo(int showId) {
        buildHolder bh = new buildHolder(); //skaber en buildholder - som holder alle informationer som vi bruger senere til at bygge scenen

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

    /**
     * Input: (ArrayList, int, String, String) arraylist with the reserved seats, int with the showid, string with the customer name, string with the customer phone number
     * Method: Inserts a reservation into the database
     * Returns: True if the reservation gets inserted. False if the reservation fails to get inserted.
     */
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


    /**
     * Input: (String) phone number.
     * Method: Finds all the reservations for a defined phone number
     * Returns: A linkedhashmap with the reservation id as key (int) and a generated string with information about the reservation (movie, time etc.)
     */
    public LinkedHashMap<Integer, HBox> getReservations(String phoneNumber) {
        LinkedHashMap<Integer, HBox> reservations = new LinkedHashMap<Integer, HBox>();
        try {
            st = getCon().createStatement();
            String query = "SELECT shows.id, time, movie_name, cinema_name, reservations.id, customer_name FROM reservations, cinemas, movies, shows WHERE reservations.customer_phone='" + phoneNumber + "' AND reservations.show_id = shows.id AND shows.movie_id = movies.id AND shows.cinema_id = cinemas.id";
            rs = st.executeQuery(query);
            while(rs.next()) {
                int id = rs.getInt("reservations.id");
                HBox hbox = new HBox(50);
                Label movieName = new Label(rs.getString("movie_name"));
                Label cinemaName = new Label(rs.getString("cinema_name"));
                Label customerName = new Label(rs.getString("customer_name"));
                Label timeStamp = new Label(new SimpleDateFormat("dd/MM HH:mm").format(rs.getTimestamp("time")));
                movieName.setPrefWidth(150);
                cinemaName.setPrefWidth(150);
                customerName.setPrefWidth(150);
                timeStamp.setPrefWidth(150);
                hbox.getChildren().addAll(movieName, timeStamp, cinemaName, customerName);
                reservations.put(id, hbox);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return reservations;
    }

    /**
     * Input: (int) reservation.
     * Method: Finds all the seats in a reservation
     * Returns: A boolean multidimensionalarray (Boolean[][]) with x being the first value and y being the second like x=5:y=3 is Boolean[5][3]
     */
    public Boolean[][] getResSeat(int reservationId) {

        try {
            st = getCon().createStatement();
            String query = "SELECT seat_x, seat_y FROM reservationlines, reservations WHERE reservationlines.reservation_id = reservations.id AND reservations.id = " + reservationId;
            rs = st.executeQuery(query);

            Boolean[][] resSeat = new Boolean[100][100];

            while(rs.next()) {
                int seat_x = rs.getInt("seat_x"); //gets the x-value of a reserved seat
                int seat_y = rs.getInt("seat_y"); // gets the y-value of a reserved seat
                resSeat[seat_x][seat_y] = true; // like resSeat[3][4] is true if reserved seat: 3:4
            }

            return resSeat;

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return null;
    }

    /**
     * Input: (int) reservation ID.
     * Method: Finds the showid for a given reservation
     * Returns: An integer with the showid
     */
    public int getShowIdFromResId(int reservationId) {

        int showId = 0;

        try {
            st = getCon().createStatement();
            String query = "SELECT shows.id FROM shows, reservations WHERE reservations.show_id = shows.id AND reservations.id = " + reservationId;
            rs = st.executeQuery(query);

            while(rs.next()) {
                showId = rs.getInt("id"); //gets the x-value of a reserved seat
            }

            return showId;

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return 0;
    }

    /**
     * Input: (ArrayList<String>, ArrayList<String>, int) first arraylist being the oldreserved seats, second arraylist being the new reserved seats, int being the id of the reservation
     * Method: Updates an order with new seats (first deleting old seats and then inserting new seats
     * Returns: Boolean - true if inserted correctly and false if not
     */
    public Boolean updateReservation(ArrayList<String> oldSeats, ArrayList<String> newSeats, int reservationId) {
        try {
            // first we delete the old reservated seats
            for(String seat : oldSeats) {
                String[] seatInfo = seat.split(":"); // here we split the string since it is like 3:3 or 9:17 so that we can get the x-value and the y-value seperated. They are now stored in an array with index 0 being the x-value and index 1 being the y-value
                String query = "DELETE FROM reservationlines WHERE reservation_id = '" + reservationId + "' AND seat_x = '" + seatInfo[0] + "' AND seat_y = '" + seatInfo[1] + "'";
                st.executeUpdate(query);
            }

            // then we insert the new reservated seats
            for(String seat : newSeats) {
                String[] seatInfo = seat.split(":"); // here we split the string since it is like 3:3 or 9:17 so that we can get the x-value and the y-value seperated. They are now stored in an array with index 0 being the x-value and index 1 being the y-value
                String query = "INSERT INTO reservationlines (reservation_id, seat_x, seat_y) VALUES ('" + reservationId + "', '" + seatInfo[0] + "', '" + seatInfo[1] + "')";
                st.executeUpdate(query);
            }

            return true;

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return false;
    }

    /**
     * Input: (ArrayList<String>, int) arraylist being the seats to delete, the int being the id of the reservation
     * Method: Deletes the seats from the reservationlines table and the reservation itself from the reservations table
     * Returns: Boolean - true if deleted correctly and false if not
     */
    public Boolean deleteReservation(ArrayList<String> oldSeats, int reservationId) {
        try {
            // deleting all the seats
            for(String seat : oldSeats) {
                String[] seatInfo = seat.split(":"); // here we split the string since it is like 3:3 or 9:17 so that we can get the x-value and the y-value seperated. They are now stored in an array with index 0 being the x-value and index 1 being the y-value
                String query = "DELETE FROM reservationlines WHERE reservation_id = '" + reservationId + "' AND seat_x = '" + seatInfo[0] + "' AND seat_y = '" + seatInfo[1] + "'";
                st.executeUpdate(query);
            }
            // deleting the reservation itself
            String query = "DELETE FROM reservations WHERE id = '" + reservationId + "'";
            st.executeUpdate(query);

            return true;

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return false;
    }

}