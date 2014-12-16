package model;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DBConnect {

    private Connection con;
    private Statement st;
    private ResultSet rs;


    // This is the constructor
    public DBConnect() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa"); // sets up a connection and stores it in the variable con
    }
    /**
     * Input: None
     * Method: Gets the current connection. If it doesn't exists/is invalid a new one is created. Parameter is seconds to wait for timeout
     * Returns: A valid connection
     */
    public Connection getCon() throws SQLException {
        if(!con.isValid(30)) { // isValid takes seconds to wait for timeout as parameter
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
        }

        return con;
    }

    /**
     * Input: None
     * Method: Gets all the movies that exists in the database
     * Returns: Linkedhashmap with the movie id(int) as key and the movie name(string) as value
     */
    public LinkedHashMap<Integer, String> getMovies() throws SQLException {
        LinkedHashMap<Integer, String> movies = new LinkedHashMap<Integer, String>(); //new linkedhashmap with movie id as key and movie name as value

        st = getCon().createStatement(); // prepares the connection for a sql statement

        String query = "SELECT * FROM movies ORDER BY movie_name ASC"; // gets all movies in alphabetic order

        rs = st.executeQuery(query);
        while(rs.next()) { // looping through the rows in the resultset
            int id = rs.getInt("id");
            String movieName = rs.getString("movie_name");
            movies.put(id, movieName);
        }

        return movies;
    }

    /**
     * Input: (int) movieId
     * Method: Gets times from every show(forestilling) a given movie(movieid) has. Sorted by time ascending
     * Returns: Linkedhashmap with the show id(int) as key and the time(timestamp) as value
     */
    public LinkedHashMap<Integer, Timestamp> getMovieSchedule(int movieId) throws SQLException {
        LinkedHashMap<Integer, Timestamp> times = new LinkedHashMap<Integer, Timestamp>();
        java.util.Date date = new java.util.Date(); // initializes a data object
        Timestamp timeNow = new Timestamp(date.getTime()); // creates a timestamp from the date object method getTime we just created above

        st = getCon().createStatement();
        String query = "SELECT * FROM shows WHERE movie_id=" + movieId + " AND time > '" + timeNow + "' ORDER BY time ASC"; // gets all shows for a specific movie where time of the show is in the future. Results are ordered by time ascending
        rs = st.executeQuery(query);
        while(rs.next()) {
            int id = rs.getInt("id");
            Timestamp timestamp = rs.getTimestamp("time");
            times.put(id, timestamp);
        }

        return times;
    }

    /**
     * Input: (int) int with the showid
     * Method: Gets information about the given show (showid) and sets it into an object that holds information like time of the show, the width of the cinema, the height of the cinema, the movie name, the cinema name, it also returns a boolean multidimensional array with the reserved seats like [2][5] = true
     * Returns: buildHolder with all the information about the show. Can be accessed like bh.getTime(), bh.getColumns, bh.getRows, bh.getMovieName, bh.getCinemaName, bh.getResSeat <- this is the reserved seats in multidimensional array
     */
    public buildHolder getBuildSceneInfo(int showId) throws SQLException {
        buildHolder bh = new buildHolder(); //skaber en buildholder - som holder alle informationer som vi bruger senere til at bygge scenen

        // first lets get the rest of the info needed to build the cinema
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

        // next lets get all reserved seats
        st = getCon().createStatement();
        query = "SELECT seat_x, seat_y FROM reservationlines, reservations WHERE reservationlines.reservation_id = reservations.id AND reservations.show_id = " + showId;
        rs = st.executeQuery(query);

        Boolean[][] resSeat = new Boolean[bh.getColumns()+1][bh.getRows()+1];
        int i = 0;
        while(rs.next()) {
            int seat_x = rs.getInt("seat_x"); //gets the x-value of a reserved seat
            int seat_y = rs.getInt("seat_y"); // gets the y-value of a reserved seat
            resSeat[seat_x][seat_y] = true; // like resSeat[3][4] is true if reserved seat: 3:4
            i++;
        }
        bh.setReservedNumber(i); // sets the number of reserved seats into the buildholder
        bh.setResSeat(resSeat); // sets the reserved seat-array into the buildholder (object)

        bh.setShowId(showId);
        return bh;
    }

    /**
     * Input: (ArrayList, int, String, String) arraylist with the reserved seats, int with the showid, string with the customer name, string with the customer phone number
     * Method: Inserts a reservation into the database
     * Returns: True if the reservation gets inserted. False if the reservation fails to get inserted.
     */
    public void insertReservation(ArrayList<String> seats, int showId, String customerName, String customerPhone) throws SQLException {
        st = getCon().createStatement();
        // simple insert statement where the order details with customer name and phone is inserted with a given showid
        String query = "INSERT INTO reservations (show_id, customer_name, customer_phone) VALUES ('" + showId + "', '" + customerName + "', '" + customerPhone + "')";
        st.executeUpdate(query);

        String lastid = getLastReservationId() + "";

        //time to insert the seats of an order - just looping through them and inserting (binded with the latest id in reservations)
        for(String seat : seats) {
            String[] seatInfo = seat.split(":"); // here we split the string since it is like 3:3 or 9:17 so that we can get the x-value and the y-value seperated. They are now stored in an array with index 0 being the x-value and index 1 being the y-value
            query = "INSERT INTO reservationlines (reservation_id, seat_x, seat_y) VALUES ('" + lastid + "', '" + seatInfo[0] + "', '" + seatInfo[1] + "')";
            st.executeUpdate(query);
        }
    }


    /**
     * Input: (String) phone number.
     * Method: Finds all the reservations for a defined phone number
     * Returns: A linkedhashmap with the reservation id as key (int) and a generated string with information about the reservation (movie, time etc.)
     */
    public LinkedHashMap<Integer, HBox> getReservations(String phoneNumber) throws SQLException {
        LinkedHashMap<Integer, HBox> reservations = new LinkedHashMap<Integer, HBox>(); // creates a linkedhashmap that stores the id of the reservation and some information about the reservation such as customer name, movie name, time etc.
        java.util.Date date= new java.util.Date(); // initializes a data object
        Timestamp timeNow = new Timestamp(date.getTime()); // creates a timestamp from the date object method getTime we just created above

        st = getCon().createStatement();
        String query = "SELECT shows.id, time, movie_name, cinema_name, reservations.id, customer_name FROM reservations, cinemas, movies, shows WHERE reservations.customer_phone='" + phoneNumber + "' AND reservations.show_id = shows.id AND shows.movie_id = movies.id AND shows.cinema_id = cinemas.id AND shows.time > '" + timeNow + "'";
        rs = st.executeQuery(query);
        while(rs.next()) {
            HBox hbox = new HBox(50); // creates a hbox that we can click on in the listview with the data
            int id = rs.getInt("reservations.id");
            Label movieName = new Label(rs.getString("movie_name")); // creates a new label with the movie name
            Label cinemaName = new Label(rs.getString("cinema_name")); // creates a new label with the name of the cinema
            Label customerName = new Label(rs.getString("customer_name")); // creates a new label with the name of the customer
            Label timeStamp = new Label(new SimpleDateFormat("dd/MM HH:mm").format(rs.getTimestamp("time"))); // creates a new label with the time of the show
            movieName.setPrefWidth(200); // sets width of the label
            cinemaName.setPrefWidth(150); // sets width of the label
            customerName.setPrefWidth(200); // sets width of the label
            timeStamp.setPrefWidth(150); // sets width of the label
            hbox.getChildren().addAll(movieName, timeStamp, cinemaName, customerName); // adds the labels to the hbox
            reservations.put(id, hbox); // adds the hbox with reservation information and the reservation id to the linkedhashmap
        }

        return reservations;
    }

    /**
     * Input: (int) reservation.
     * Method: Finds all the seats in a reservation
     * Returns: A boolean multidimensionalarray (Boolean[][]) with x being the first value and y being the second like x=5:y=3 is Boolean[5][3]
     */
    public Boolean[][] getResSeat(int reservationId, buildHolder bh) throws SQLException {
        st = getCon().createStatement();
        String query = "SELECT seat_x, seat_y FROM reservationlines, reservations WHERE reservationlines.reservation_id = reservations.id AND reservations.id = " + reservationId;
        rs = st.executeQuery(query);

        Boolean[][] resSeat = new Boolean[bh.getColumns()+1][bh.getRows()+1]; // multidimensional array is based on 0-index. therefore we add 1 to the lenght

        while(rs.next()) {
            int seat_x = rs.getInt("seat_x"); //gets the x-value of a reserved seat
            int seat_y = rs.getInt("seat_y"); // gets the y-value of a reserved seat
            resSeat[seat_x][seat_y] = true; // like resSeat[3][4] is true if reserved seat: 3:4
        }

        return resSeat;
    }

    /**
     * Input: (int) reservation ID.
     * Method: Finds the showid for a given reservation
     * Returns: An integer with the showid
     */
    public int getShowIdFromResId(int reservationId) throws SQLException {

        int showId = 0;

        st = getCon().createStatement();
        String query = "SELECT shows.id FROM shows, reservations WHERE reservations.show_id = shows.id AND reservations.id = " + reservationId;
        rs = st.executeQuery(query);

        while(rs.next()) {
            showId = rs.getInt("id"); //gets the x-value of a reserved seat
        }

        return showId;
    }

    /**
     * Input: (ArrayList<String>, ArrayList<String>, int) first arraylist being the oldreserved seats, second arraylist being the new reserved seats, int being the id of the reservation
     * Method: Updates an order with new seats (first deleting old seats and then inserting new seats
     * Returns: Boolean - true if inserted correctly and false if not
     */
    public void updateReservation(ArrayList<String> newSeats, int reservationId) throws SQLException {
        // first we delete the old reservated seats
        st.executeUpdate("DELETE FROM reservationlines WHERE reservation_id = '" + reservationId + "'");

        // then we insert the new reservated seats
        for(String seat : newSeats) {
            String[] seatInfo = seat.split(":"); // here we split the string since it is like 3:3 or 9:17 so that we can get the x-value and the y-value seperated. They are now stored in an array with index 0 being the x-value and index 1 being the y-value
            String query = "INSERT INTO reservationlines (reservation_id, seat_x, seat_y) VALUES ('" + reservationId + "', '" + seatInfo[0] + "', '" + seatInfo[1] + "')";
            st.executeUpdate(query);
        }
    }

    /**
     * Input: (ArrayList<String>, int) arraylist being the seats to delete, the int being the id of the reservation
     * Method: Deletes the seats from the reservationlines table and the reservation itself from the reservations table
     * Returns: Boolean - true if deleted correctly and false if not
     */
    public Boolean deleteReservation(int reservationId) throws SQLException {
        // deleting all the seats
        st.executeUpdate("DELETE FROM reservationlines WHERE reservation_id = '" + reservationId + "'");
        // deleting the reservation itself
        st.executeUpdate("DELETE FROM reservations WHERE id = '" + reservationId + "'");

        return true;
    }

    /**
     * Input: String with the phone number of the reservation
     * Method: Finds all the reservations for a specific phone number
     * Returns: Boolean - true if any reservation is found and false if not
     */
    public Boolean getBooleanReservations(String phoneNumber) throws SQLException {
        st = getCon().createStatement();
        String query = "SELECT shows.id, time, movie_name, cinema_name, reservations.id, customer_name FROM reservations, cinemas, movies, shows WHERE reservations.customer_phone='" + phoneNumber + "' AND reservations.show_id = shows.id AND shows.movie_id = movies.id AND shows.cinema_id = cinemas.id";
        rs = st.executeQuery(query);
        while(rs.next()) {
            return true;
        }

        return false;
    }

    /**
     * Input: None
     * Method: Gets the id of the last inserted reservation
     * Returns: Integer - corresponding the last inserted reservation id in the reservations table
     */
    public int getLastReservationId() throws SQLException {
        rs = st.executeQuery("SELECT id FROM reservations ORDER BY id DESC LIMIT 1");
        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }


}