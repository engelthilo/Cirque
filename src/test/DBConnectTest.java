package test;


import controller.Controller;
import model.DBConnect;
import org.junit.Test;
import sun.awt.image.ImageWatched;
import static org.junit.Assert.assertEquals;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//denne test tjekker om film og ide passer med det forventede.

public class DBConnectTest {
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



        DBConnect dbConnect = new DBConnect();

        assertEquals(expected, dbConnect.getMovies());
    }
}





