package test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.lang.Exception;
import java.lang.Integer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

public class MockitoTest {

    public void testConnect() throws Exception {

    }
    @Test
            public testGetCinema() throws Exception{
        String name= "Sal 1";
        try{
            Resultset mockResultSet = mock(ResultSet.class);
                when(mockResultSet.getString("cinema_name")).thenReturn("Sal 1");
                when(mockResultSet.getInt("number_of_rows")).thenReturn("10");
                when(mockResultSet.getInt("seats_in_row")).thenReturn(15);
                when(mockResultSet.next()).thenReturn(true, false);

            Statement mockStatement = mock(Statement.class);
                when(mockStatement.executeQuery("SELECT * FROM cinemas WHERE cinema_name='" + name +"'")).thenReturn(mockResultSet);

            Connection mockConnection = mock(Statement.class);
                when(mockConnection.createStatement()),thenReturn(mockStatement);

            MockitoTest mgr = new MockitoTest(mock Connection);
            Integer[] Expeceted = new Integer[10];
            Expeceted[0] = 10;
            Expeceted[1]= 8;
            System.out.println("virker");
            Assert.assertArrayEwuals(Expeceted, Actual);
        }catch (SQLException e){
            System.out.println("Error: " + e);
        }

    }
}
