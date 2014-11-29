package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
        LinkedHashMap<Integer, String> curMovies = new LinkedHashMap(db.getMovies()); //Holder en dobbelt - indsættelse: rækkefølge.

        //For in loop that finds and creates a button for every movie in the database [[MARK]]
        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) { //Loop sætter key og værdi på film - Linkedhasmap
            final Button button = new Button(movie.getValue()); //Laver en knap med filmværdi
            button.setPrefWidth(200); //sætter størrelse på film-knapperne
            showsVBox.getChildren().add(button);  //henter "børn" - dvs knapperne med film
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { //når der bliver klikket med musen på en af filmknapperne
                    scheduleHeader.setText("Viser forestillinger for filmen: " + movie.getValue());
                    getMovieSchedule(movie.getKey()); //henter data om shows fra databasen
                    //Calls the method getMovieSchedule that creates new buttons from the database
                    //with time and date for play
                }
            });
        }

    }

    @FXML
    private void getMovieSchedule(int movieId) { //henter spilletider for film fra databasen via movieID
        //Reset the schedule boxes
        upper_schedule.getChildren().clear(); //henter 7 dage (0-6) med tidnpunker ind på upper_scheule
        lower_schedule.getChildren().clear(); //henter 7 dage (7-13) med tidspunker ind på lower_schedule

        LinkedHashMap<Integer, Timestamp> schedule = new LinkedHashMap(db.getMovieSchedule(movieId));
        //henter filmspilletiderne mm ind i et linkedhasmap

        //Jeg er lidt i tvivl om hvad der foregår her? Jeg forstår princippet men vil gerne have det forklaret bedre [[MARK]]
        Timestamp[][] times = new Timestamp[14][6]; //sætter tiderne fra databsen i rækkefølge efter tid.
        int[][] showIds = new int[14][6];  //14 står for 14 dage - 6 for de 6 shows der bliver vist pr dag pr sal.
        int i = -1; //starter ved -1 fordi vi vil starte ved nr. 0. i er datoen
        int j = 0; //j er måned
        String lastShow = "";

        //Runs the schedule for the selected movie through and print out the date for them
        //Gerne forklar skridtene lidt bedre her [[Mark]]
        for(Map.Entry<Integer, Timestamp> show : schedule.entrySet()) { ////Loop der kører sålænge tiden er voksende - dvs at film ikke er den sidste den pågældende dag.
            String newShow = new SimpleDateFormat("dd/MM").format(show.getValue()); //sætter tiden fra getValue til at vise dato og måned
            if(!newShow.equals(lastShow)) { //hvis ikke de nye show er det samme som de sidste gør dette:
                i++; //fortsæt loppet
                j = 0; //sætter måned til 0 - dvs den samme dato som showet før.
                times[i][j] = show.getValue(); //hent tiden på det nye show
                showIds[i][j] = show.getKey(); //henter id på shows - dvs navn, sal mm.
                j++; //forøg måned tilsidst.
            } else {
                times[i][j] = show.getValue(); //hent tiden på film
                showIds[i][j] = show.getKey(); //hent id på film
                j++; //gå til næste måned
            }
            lastShow = newShow;
        }

        for(i = 0; i < 14; i++) {
            // her skal oprettes ny container til en ny dag så længe vi er under de 14 dage vores skema kører.

            if((times[i][0] != null) && (i<7)) { // i = dag. når det er fra dag 0-6
                TitledPane tp = new TitledPane(); //hvad er titlepane?
                tp.setText(new SimpleDateFormat("dd/MM").format(times[i][0])); //viser data og måned
                VBox vb = new VBox(); //sætter en vbox ind i upper_schedule
                vb.setSpacing(15); //sætter mellemrum mellem kolonner med tiderne
                for (j = 0; j < 6; j++) {
                    // her oprettes de individuelle tider for filmen den pågældende dag. j = tidspunkt

                    if (times[i][j] != null) { //hvis tid og dato ikke er nul.
                        final int showId = showIds[i][j]; //henter showsID
                        final Button button = new Button(new SimpleDateFormat("HH:mm").format(times[i][j])); //laver knapper med spilletidspunker
                        button.setPrefWidth(100); //sætter størrelse på tid-knapperne
                        button.setPadding(new Insets(10, 10, 10, 10)); //sætter padding på knapperne
                        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() { //laver event når man klikker på musen
                            @Override
                            public void handle(MouseEvent event) {
                                buildReservationScene(showId);
                            } //ved eventet bliver den pågældende resevations side bygget - alt efter hvilken film
                        });
                        vb.getChildren().add(button); //vb = vbox - her sættes "børn" af boksen ind på knapper - børn = de film der hører til boksen
                    }
                }
                tp.setContent(vb); //hvad sker der her??
                upper_schedule.getChildren().add(tp); //henter film til upper_schedule og tiæføjer til TitledPane tp
            }

            //samme som oppe over - bare for lower_schedule og dagene fra 7-13
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
    private void buildReservationScene(int showId) { //denne metode bygger reservationScene for den pågældende film

        //dbcall så vi kan få information om forestillingen (hvor den vises, filmtitel osv.)
        sceneVBox.getChildren().clear(); //henter sædderne ind på sceneVBox - hvad gør clear??
        buildHolder bh = db.getBuildSceneInfo(showId); //lægger infor om den enkelte films sceneopbygning ind i buildHolder
        movieNameLabel.setText(bh.getMovieName()); //sætter navnet på den pågældende film ind på movieNameLabel
        movieTimeLabel.setText("Tidspunkt: " + new SimpleDateFormat("dd/MM HH:mm").format(bh.getTime())); //viser tidspunkt for det show man ahr valgt
        cinemaNameLabel.setText(bh.getCinemaName()); //sætter navn på den pågældende sal
        int totalSeats = (bh.getColumns() * bh.getRows()); //regner antalet af total antal sædder i salen ud.
        int freeSeats = totalSeats - bh.getReservedNumber(); //regner antal af frie sædder ud.
        numberOfReserved.setText("Ledige pladser: " + freeSeats); //viser antal frie sædder i den pågældende sal
        numberOfTotal.setText("Pladser i alt: " + totalSeats); //viser antal af sædder i den pågældende sal
        tabPane.getSelectionModel().select(1); //sætter det ind under "fane" nr 2.

        int columns = bh.getColumns(); //henter kolonner i den pågældende sal
        int rows = bh.getRows(); //henter rækker i den pågældende sal
        GridPane gp = new GridPane(); //skaber et gridpane som kolonner og rækker kan opbevares i.
        gp.setPrefSize(scenePane.getPrefWidth(),scenePane.getPrefHeight()); //sætter gridpane til samme str som det scenePane den ligger inden i.
        gp.setHgap(5); //sætter mellemrum mellem sædderne på horizontalt led
        gp.setVgap(5); //sætter mellemrum mellem sædderne på vertikalt ved.
        gp.setAlignment(Pos.CENTER); // centers the gridpane to the vbox
        int[] reserved_x = bh.getReserved_x(); //henter information om reseveret sædder på x's plads.
        int[] reserved_y = bh.getReserved_y(); //henter information om reseveret sædder på y's plads.
        for(int i = 1; i < columns+1; i++) { //laver en forloppe der kører kolonerne igennem

            for(int j = 1; j < rows+1; j++) { //forlopp der kører rækkerne igennem
                double width = (scenePane.getWidth()-6*bh.getColumns()-6)/bh.getColumns(); // sets the width of the seat according to the cinema width
                double height = (scenePane.getHeight()-6*bh.getRows()-6)/bh.getRows(); // sets the height of the seat according to the cinema height
                Rectangle r = new Rectangle(width,height); //laver sædderne som firkanter
                if((reserved_x[i] == 1) && (reserved_y[j-1] == 1)) { //hvis sædderne er reseveret bliver de røde
                    r.setFill(Color.RED);
                    //r.setStyle("-fx-background-color: #F44336");
                } else {
                    r.setFill(Color.GREEN); //hvis de ikke er reseveret bliver de grønne
                    //r.setStyle("-fx-background-color: #4CAF50");
                }


                gp.add(r, i, j); //tilføj til gridpane: r= firkanterne, i=fælterne på x-aksen og j= felterne på y-aksen
            }

        }

        sceneVBox.getChildren().add(gp); //sætter gridpane ind i sceneVBox

    }

}
