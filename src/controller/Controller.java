package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.DBConnect;
import model.buildHolder;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private VBox showsVBox;

    @FXML
    private HBox upper_schedule;

    @FXML
    private HBox lower_schedule;

    @FXML
    private Label movieNameLabel;

    @FXML
    private Label scheduleHeader;

    @FXML
    private Label movieTimeLabel;

    @FXML
    private Label cinemaNameLabel;

    @FXML
    private Label numberOfReserved;

    @FXML
    private Label numberOfTotal;

    @FXML
    private Pane scenePane;

    @FXML
    private VBox sceneVBox;


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

        //For in loop that finds and creates a button for every movie in the database [[MARK]]
        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) {
            final Button button = new Button(movie.getValue());
            button.setPrefWidth(200); //sætter størrelse på film-knapperne
            showsVBox.getChildren().add(button);
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    scheduleHeader.setText("Viser forestillinger for filmen: " + movie.getValue());
                    getMovieSchedule(movie.getKey());
                    //Calls the method getMovieSchedule that creates new buttons from the database
                    //with time and date for play
                }
            });
        }

    }

    @FXML
    private void getMovieSchedule(int movieId) {
        //Reset the schedule boxes
        upper_schedule.getChildren().clear();
        lower_schedule.getChildren().clear();

        LinkedHashMap<Integer, Timestamp> schedule = new LinkedHashMap(db.getMovieSchedule(movieId));

        //Jeg er lidt i tvivl om hvad der foregår her? Jeg forstår princippet men vil gerne have det forklaret bedre [[MARK]]
        Timestamp[][] times = new Timestamp[14][6];
        int[][] showIds = new int[14][6];
        int i = -1;
        int j = 0;
        String lastShow = "";

        //Runs the schedule for the selected movie through and print out the date for them
        //Gerne forklar skridtene lidt bedre her [[Mark]]
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
                        button.setPadding(new Insets(10, 10, 10, 10));
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
        sceneVBox.getChildren().clear();
        buildHolder bh = db.getBuildSceneInfo(showId);
        movieNameLabel.setText(bh.getMovieName());
        movieTimeLabel.setText("Tidspunkt: " + new SimpleDateFormat("dd/MM HH:mm").format(bh.getTime()));
        cinemaNameLabel.setText(bh.getCinemaName());
        int totalSeats = (bh.getColumns() * bh.getRows());
        int freeSeats = totalSeats - bh.getReservedNumber();
        numberOfReserved.setText("Ledige pladser: " + freeSeats);
        numberOfTotal.setText("Pladser i alt: " + totalSeats);
        tabPane.getSelectionModel().select(1);

        int columns = bh.getColumns();
        int rows = bh.getRows();
        GridPane gp = new GridPane();
        gp.setPrefSize(scenePane.getPrefWidth(),scenePane.getPrefHeight());
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setAlignment(Pos.CENTER); // centers the gridpane to the vbox
        int[] reserved_x = bh.getReserved_x();
        int[] reserved_y = bh.getReserved_y();
        for(int i = 1; i < columns+1; i++) {

            for(int j = 1; j < rows+1; j++) {
                double width = (scenePane.getWidth()-6*bh.getColumns()-6)/bh.getColumns(); // sets the width of the seat according to the cinema width
                double height = (scenePane.getHeight()-6*bh.getRows()-6)/bh.getRows(); // sets the height of the seat according to the cinema height
                Rectangle r = new Rectangle(width,height);
                if((reserved_x[i] == 1) && (reserved_y[j-1] == 1)) {
                    r.setFill(Color.RED);
                    //r.setStyle("-fx-background-color: #F44336");
                } else {
                    r.setFill(Color.GREEN);
                    //r.setStyle("-fx-background-color: #4CAF50");
                }


                gp.add(r, i, j);
            }

        }

        sceneVBox.getChildren().add(gp);

        //måske lave et gridpane som vi kan bygge sæderne i?

    }

}
