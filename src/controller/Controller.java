package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import model.DBConnect;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @FXML
    private Label movieNameLabel;

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
        LinkedHashMap<Integer, String> curMovies = new LinkedHashMap(db.getMovies());

        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) {
            final Button button = new Button(movie.getValue());
            button.setPrefWidth(200); //sætter størrelse på film-knapperne
            showsVBox.getChildren().add(button);
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    getMovieSchedule(movie.getKey());
                }
            });
        }

    }

    @FXML
    private void getMovieSchedule(int movieId) {
        upper_schedule.getChildren().clear();

        LinkedHashMap<Integer, Timestamp> schedule = new LinkedHashMap(db.getMovieSchedule(movieId));

        Timestamp[][] times = new Timestamp[14][6];
        int[][] showIds = new int[14][6];
        int i = -1;
        int j = 0;
        String lastShow = "";

        for(Map.Entry<Integer, Timestamp> show : schedule.entrySet()) {
            String newShow = new SimpleDateFormat("dd/MM").format(show.getValue());
            if(!newShow.equals(lastShow)) {
                i++;
                j = 0;
                times[i][j] = show.getValue();
                showIds[i][j] = show.getKey();
                j++;
            } else {
                times[i][j] = show.getValue();
                showIds[i][j] = show.getKey();
                j++;
            }
            lastShow = newShow;
        }

        for(i = 0; i < 14; i++) {
            // her skal oprettes ny container til en ny dag

            if((times[i][0] != null) && (i<7)) {
                TitledPane tp = new TitledPane();
                tp.setText(new SimpleDateFormat("dd/MM").format(times[i][0]));
                VBox vb = new VBox();
                vb.setSpacing(15);
                for (j = 0; j < 6; j++) {
                    // her oprettes de individuelle tider for filmen den pågældende dag

                    if (times[i][j] != null) {
                        final int showId = showIds[i][j];
                        final Button button = new Button(new SimpleDateFormat("HH:mm").format(times[i][j]));
                        button.setPrefWidth(100); //sætter størrelse på tid-knapperne
                        button.setPadding(new Insets(15, 15, 15, 15));
                        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                buildReservationScene(showId);
                            }
                        });
                        vb.getChildren().add(button);
                    }
                }
                tp.setContent(vb);
                upper_schedule.getChildren().add(tp);
            }
            if((times[i][0] != null) && (i>=7)) {
                TitledPane tp = new TitledPane();
                tp.setText(new SimpleDateFormat("dd/MM").format(times[i][0]));
                VBox vb = new VBox();
                vb.setSpacing(15);
                for (j = 0; j < 6; j++) {
                    // her oprettes de individuelle tider for filmen den pågældende dag

                    if (times[i][j] != null) {
                        final int showId = showIds[i][j];
                        final Button button = new Button(new SimpleDateFormat("HH:mm").format(times[i][j]));
                        button.setPrefWidth(100); //sætter størrelse på tid-knapperne
                        button.setPadding(new Insets(15, 15, 15, 15));
                        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                buildReservationScene(showId);
                            }
                        });
                        vb.getChildren().add(button);
                    }
                }
                tp.setContent(vb);
                lower_schedule.getChildren().add(tp);
            }
        }

    }

    @FXML
    private void buildReservationScene(int showId) {

        //dbcall så vi kan få information om forestillingen (hvor den vises, filmtitel osv.)

        movieNameLabel.setText("Test");
        tabPane.getSelectionModel().select(1);
    }

}
