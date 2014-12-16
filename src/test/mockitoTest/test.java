package dk.itu.biograftprojekt.dk.itu.biografprojekt.Mysql;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class dbManagerTest  {

    public void testConnect() throws Exception {

    }

    @Test
    public void testGetCinemaSeats() throws Exception {
        String name = "Cinema 1";
        try {
            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getString("cinema_name")).thenReturn("Cinema 1");
            when(mockResultSet.getInt("number_of_rows")).thenReturn(5);
            when(mockResultSet.getInt("seats_per_row")).thenReturn(8);
            when(mockResultSet.next()).thenReturn(true, false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT * FROM cinemas WHERE cinema_name = '" + name +"'")).thenReturn(mockResultSet);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            Integer[] Actual = mgr.getCinemaSeats(name);

            Integer[] Expected = new Integer[10];
            Expected[0] = 5;
            Expected[1] = 8;

            Assert.assertArrayEquals(Expected, Actual);

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

    @Test
    public void testGetAllReservedSeats() throws Exception {
        try {
            String[] info = new String[3];
            info[0] = "InterStellar";
            info[1] = "12-12/14";
            info[2] = "12:00";
            int movie_id = 1;
            int show_id = 1;
            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getInt("seat_number")).thenReturn(5, 8, 6);
            when(mockResultSet.getInt("row_number")).thenReturn(8, 6, 5);
            when(mockResultSet.next()).thenReturn(true, true, true, false);

            ResultSet mockResultSet2 = mock(ResultSet.class);
            when(mockResultSet2.getInt("show_id")).thenReturn(1);
            when(mockResultSet2.next()).thenReturn(true, false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT * FROM chairs WHERE show_id = "+show_id)).thenReturn(mockResultSet);
            when(mockStatement.executeQuery("SELECT * FROM shows WHERE play_date = '"+info[1]+"' AND play_time = '" + info[2]+"' AND movie_id = "+movie_id)).thenReturn(mockResultSet2);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            ArrayList<Integer> Actual = mgr.getAllReservedSeats(info);

            ArrayList<Integer> Expected = new ArrayList<Integer>();
            Expected.add(5);
            Expected.add(8);
            Expected.add(8);
            Expected.add(6);
            Expected.add(6);
            Expected.add(5);

            Assert.assertEquals(Expected, Actual);

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

    @Test
    public void testAddToTest2() throws Exception {
        try {
            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getString("title")).thenReturn("Interstellar", "Hunger Games", "Gone Girl");
            when(mockResultSet.getInt("cinema_id")).thenReturn(1, 2, 3);
            when(mockResultSet.getString("play_time")).thenReturn("12:00", "15:00", "19:00");
            when(mockResultSet.getString("play_date")).thenReturn("12-12/14", "14-12/14", "15-12/14");
            when(mockResultSet.getString("cinema_name")).thenReturn("Cinema 1", "Cinema 2", "Cinema 3");
            when(mockResultSet.next()).thenReturn(true, true, true,false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT movies.title, shows.play_date, shows.play_time, cinemas.cinema_name, cinemas.cinema_id FROM movies, shows, cinemas WHERE movies.movie_id = shows.movie_id AND shows.cinema_id = cinemas.cinema_id")).thenReturn(mockResultSet);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            mgr.deleteObservableListShow();
            mgr.addToTest2();
            ObservableList<Show> Actual = mgr.getListMovie();

            ObservableList<Show> Expected = FXCollections.observableArrayList();
            Expected.add(new Show("Interstellar", "12-12/14", "12:00", "Cinema 1"));
            Expected.add(new Show("Hunger Games", "14-12/14", "15:00", "Cinema 2"));
            Expected.add(new Show("Gone Girl", "15-12/14", "19:00", "Cinema 3"));

            Assert.assertEquals(Expected.get(0).getMovieName(), Actual.get(0).getMovieName());
            Assert.assertEquals(Expected.get(1).getMovieName(), Actual.get(1).getMovieName());
            Assert.assertEquals(Expected.get(2).getMovieName(), Actual.get(2).getMovieName());

            Assert.assertEquals(Expected.get(0).getMoviedate(), Actual.get(0).getMoviedate());
            Assert.assertEquals(Expected.get(1).getMoviedate(), Actual.get(1).getMoviedate());
            Assert.assertEquals(Expected.get(2).getMoviedate(), Actual.get(2).getMoviedate());

            Assert.assertEquals(Expected.get(0).getMovieTime(), Actual.get(0).getMovieTime());
            Assert.assertEquals(Expected.get(1).getMovieTime(), Actual.get(1).getMovieTime());
            Assert.assertEquals(Expected.get(2).getMovieTime(), Actual.get(2).getMovieTime());

            Assert.assertEquals(Expected.get(0).getCinema(), Actual.get(0).getCinema());
            Assert.assertEquals(Expected.get(1).getCinema(), Actual.get(1).getCinema());
            Assert.assertEquals(Expected.get(2).getCinema(), Actual.get(2).getCinema());

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

    public void testAddToTest() throws Exception {
        /*TODO Basicly the same as testAddToTest*/
    }

    @Test
    public void testGetAllInfoOnePerson() throws Exception {
        try {
            String phone_nr = "88888888";

            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getString("person_name")).thenReturn("Mads", "Mads");
            when(mockResultSet.getInt("row_number")).thenReturn(3, 1);
            when(mockResultSet.getInt("seat_number")).thenReturn(4, 2);
            when(mockResultSet.getString("play_time")).thenReturn("12:00", "12:00");
            when(mockResultSet.getString("play_date")).thenReturn("12-12/14", "12-12/14");
            when(mockResultSet.getString("title")).thenReturn("Interstellar", "Interstellar");
            when(mockResultSet.getString("phone_number")).thenReturn("88888888", "88888888");
            when(mockResultSet.next()).thenReturn(true, true, false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT persons.person_id, persons.phone_number, persons.person_name, shows.movie_id, shows.show_id, shows.play_date, movies.title, shows.play_time, chairs.row_number, chairs.seat_number FROM persons,chairs,shows,movies WHERE persons.person_id = chairs.person_id AND chairs.show_id = shows.show_id AND shows.show_id = movies.movie_id AND persons.phone_number = " + phone_nr)).thenReturn(mockResultSet);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            mgr.getAllInfoOnePerson(phone_nr);
            ObservableList<Reservation> Actual = mgr.getList();

            ObservableList<Reservation> Expected = FXCollections.observableArrayList();
            Expected.add(new Reservation("Mads", "Interstellar", "12-12/14", "12:00", 3, 4, "88888888"));
            Expected.add(new Reservation("Mads", "Interstellar", "12-12/14", "12:00", 1, 2, "88888888"));

            Assert.assertEquals(Expected.get(0).getName(), Actual.get(0).getName());
            Assert.assertEquals(Expected.get(1).getName(), Actual.get(1).getName());

            Assert.assertEquals(Expected.get(0).getMovie(), Actual.get(0).getMovie());
            Assert.assertEquals(Expected.get(1).getMovie(), Actual.get(1).getMovie());

            Assert.assertEquals(Expected.get(0).getDate(), Actual.get(0).getDate());
            Assert.assertEquals(Expected.get(1).getDate(), Actual.get(1).getDate());

            Assert.assertEquals(Expected.get(0).getTime(), Actual.get(0).getTime());
            Assert.assertEquals(Expected.get(1).getTime(), Actual.get(1).getTime());

            Assert.assertEquals(Expected.get(0).getRow(), Actual.get(0).getRow());
            Assert.assertEquals(Expected.get(1).getRow(), Actual.get(1).getRow());

            Assert.assertEquals(Expected.get(0).getSeat(), Actual.get(0).getSeat());
            Assert.assertEquals(Expected.get(1).getSeat(), Actual.get(1).getSeat());

            Assert.assertEquals(Expected.get(0).getPhoneNumber(), Actual.get(0).getPhoneNumber());
            Assert.assertEquals(Expected.get(1).getPhoneNumber(), Actual.get(1).getPhoneNumber());

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

    @Test
    public void testGetMovieID() throws Exception {
        try {
            String movieTitle = "Interstellar";

            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getInt("movie_id")).thenReturn(2);
            when(mockResultSet.next()).thenReturn(true, false);


            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT * FROM movies WHERE movies.title = '" + movieTitle+"'")).thenReturn(mockResultSet);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            int Actual = mgr.getMovieID(movieTitle);

            int Expected = 2;

            Assert.assertEquals(Expected, Actual);
        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

    @Test
    public void testGetShowID() throws Exception {
        try {
            String playDate = "12-12/14";
            String playTime = "19:00";
            String movieTitle = "Interstellar";
            int movie_id = 2;

            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getInt("movie_id")).thenReturn(2);
            when(mockResultSet.next()).thenReturn(true, false);


            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT * FROM movies WHERE movies.title = '" + movieTitle+"'")).thenReturn(mockResultSet);


            ResultSet mockResultSet2 = mock(ResultSet.class);
            when(mockResultSet2.getInt("show_id")).thenReturn(2);
            when(mockResultSet2.next()).thenReturn(true, false);


            when(mockStatement.executeQuery("SELECT * FROM shows WHERE play_date = '"+playDate+"' AND play_time = '"+playTime+"' AND movie_id = "+movie_id)).thenReturn(mockResultSet2);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            int Actual = mgr.getShowID(movieTitle, playDate, playTime);

            int Expected = 2;

            Assert.assertEquals(Expected, Actual);
        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

    @Test
    /*TODO Find out why it gives at nullpoint exception but gives the right result */
    public void testGetCinemaID() throws Exception {
        String play_date = "12-12/14";
        String play_time = "12:00";
        String title = "Interstellar";
        int movie_id = 2;
        try {
            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getInt("cinema_id")).thenReturn(1);
            when(mockResultSet.next()).thenReturn(true, false);

            ResultSet mockResultSet2 = mock(ResultSet.class);
            when(mockResultSet2.getInt("movie_id")).thenReturn(1);
            when(mockResultSet2.next()).thenReturn(true, false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT * FROM shows WHERE play_date = '"+play_date+"' AND play_time ='"+ play_time +"' AND movie_id = "+ movie_id)).thenReturn(mockResultSet);
            when(mockStatement.executeQuery("SELECT * FROM movies WHERE movies.title = '" + title+"'")).thenReturn(mockResultSet2);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            int Actual = mgr.getCinemaID(play_date,play_time,title);

            int Expected = 1;

            Assert.assertEquals(Expected, Actual);

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }


    @Test
    public void testGetPersonID() throws Exception {
        try {
            ResultSet mockResultSet = mock(ResultSet.class);
            when(mockResultSet.getInt("person_id")).thenReturn(1);
            when(mockResultSet.getString("person_name")).thenReturn("Mads");
            when(mockResultSet.next()).thenReturn(true, false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT * FROM persons, chairs WHERE persons.phone_number = '88888888'")).thenReturn(mockResultSet);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            Integer[] Actual = mgr.getPersonID("88888888");

            Integer[] Expected = new Integer[1];
            Expected[0] = 1;

            Assert.assertEquals(Expected[0], Actual[0]);

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }


    /*TODO find out this fucks with addtotest2 test*/

    public void testGetMovieName() throws Exception {
        try {
            String movie_name = "Interstellar";

            ResultSet mockResultSet = mock(ResultSet.class);

            when(mockResultSet.getString("title")).thenReturn("Interstellar");
            when(mockResultSet.getInt("cinema_id")).thenReturn(1);
            when(mockResultSet.getString("play_time")).thenReturn("12:00");
            when(mockResultSet.getString("play_date")).thenReturn("12-12/14");
            when(mockResultSet.getString("cinema_name")).thenReturn("Cinema 1");
            when(mockResultSet.next()).thenReturn(true, false);

            Statement mockStatement = mock(Statement.class);
            when(mockStatement.executeQuery("SELECT movies.title, shows.play_date, shows.play_time, cinemas.cinema_name, cinemas.cinema_id FROM movies, shows, cinemas WHERE movies.movie_id = shows.movie_id AND shows.cinema_id = cinemas.cinema_id AND movies.title = '"+movie_name+"'")).thenReturn(mockResultSet);

            Connection mockConnection = mock(Connection.class);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            MockitodbManager mgr = new MockitodbManager(mockConnection);
            mgr.getMovieName(movie_name);
            ObservableList<Show> Actual1 = mgr.getListMovie();
            mgr.deleteObservableListShow();

            ObservableList<Show> Expected = FXCollections.observableArrayList();
            Expected.add(new Show("Interstellar", "12-12/14", "12:00", "Cinema 1"));

            Assert.assertEquals(Expected.get(0).getMovieName(), Actual1.get(0).getMovieName());

            Assert.assertEquals(Expected.get(0).getMoviedate(), Actual1.get(0).getMoviedate());

            Assert.assertEquals(Expected.get(0).getMovieTime(), Actual1.get(0).getMovieTime());

            Assert.assertEquals(Expected.get(0).getCinema(), Actual1.get(0).getCinema());

        } catch (SQLException e) {
           /*Cannot happen in this case*/
        }
    }

}