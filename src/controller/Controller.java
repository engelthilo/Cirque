package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    @FXML
    private Pane pane1;

    @FXML
    private TabPane tabPane;

    @FXML
    private SplitPane showPane;

    @FXML
    private VBox showsVBox;

    @FXML
    private HBox upper_schedule;

    @FXML
    private HBox lower_schedule;

    private DBConnect db;

    public Controller() {
        db = new DBConnect();
    }


    @FXML
    private void initialize() {
        getMovies();
    }

    @FXML
    private void getMovies() {
        HashMap<Integer, String> curMovies = new HashMap(db.getMovies());

        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) {
            final Button button = new Button(movie.getValue());
            showsVBox.getChildren().add(button);
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    getMovieSchedule(movie.getKey());
                    System.out.println("MovieID: " + movie.getKey());
                    System.out.println("MovieName: " + movie.getValue());
                }
            });
        }

    }

    @FXML
    private void getMovieSchedule(int movieId) {
        upper_schedule.getChildren().clear();

        //ArrayList<Timestamp> schedule = new ArrayList<Timestamp>(db.getMovieSchedule(movieId));
        HashMap<Integer, Timestamp> schedule = new HashMap(db.getMovieSchedule(movieId));

        Timestamp[][] array = new Timestamp[14][6];
        int i = -1;
        int j = 0;
        String lastShow = "";
        for(Map.Entry<Integer, Timestamp> show : schedule.entrySet()) {
            String newShow = new SimpleDateFormat("dd/MM").format(show.getValue());
            if(!newShow.equals(lastShow)) {
                i++;
                j = 0;
                array[i][j] = show.getValue();
                j++;
            } else {
                array[i][j] = show.getValue();
                j++;
            }
            lastShow = newShow;
        }

        for(i = 0; i < 6; i++) {
            //System.out.println(array[0][i]);
        }
        System.out.println();
        for(i = 0; i < 14; i++) {
            // her skal oprettes ny container til en ny dag

            for (j = 0; j < 6; j++) {
                System.out.println(array[i][j]);
            }


            /*if(array[i][0] != null) {
                TitledPane tp = new TitledPane();
                tp.setText(new SimpleDateFormat("dd/MM").format(array[i][0]));
                VBox vb = new VBox();
                for (j = 0; j < 6; j++) {
                    // her oprettes de individuelle tider for filmen den pågældende dag

                    if (array[i][j] != null) {
                        System.out.println(array[i][j]);
                        final Button button = new Button(new SimpleDateFormat("HH:mm").format(array[i][j]));
                        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                tabPane.getSelectionModel().select(1);
                            }
                        });
                        vb.getChildren().add(button);
                    }
                }
                tp.setContent(vb);
                upper_schedule.getChildren().add(tp);
            }*/
        }

    }

}
