package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private Label numberOfSelectedSeats;

    @FXML
    private Pane scenePane;

    @FXML
    private VBox sceneVBox;

    @FXML
    private TextField customerName;

    @FXML
    private TextField customerPhone;

    @FXML
    private Pane overfillPane;

    @FXML
    private ListView reservationList;

    @FXML
    private TextField phoneNumber;

    private DBConnect db;

    private ArrayList<String> seatsInOrder;

    private buildHolder bh;

    private Boolean dragMouse;

    private int intChosenSeats;

    public Controller() {
        db = new DBConnect();
    }


    @FXML
    private void initialize() {
        getMovies();
    }


    @FXML
    protected void getMovies() {
        LinkedHashMap<Integer, String> curMovies = new LinkedHashMap(db.getMovies()); //Holds movieid and moviename (int og string) - LinkedHashMap da rækkefølgen er vigtig (alfabetisk)

        //For loop that finds and creates a button for every movie in the database [[MARK]]
        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) { //Loops through the linkedhasmap på film
            final Button button = new Button(movie.getValue()); //creates a button with text of the movie
            button.setPrefWidth(200); //sets the width of the buttons
            showsVBox.getChildren().add(button);  //gets the actual container of the element that holds the buttons and apply the newly created button
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { //event when clicked on the created button
                    scheduleHeader.setText("Viser forestillinger for filmen: " + movie.getValue());
                    getMovieSchedule(movie.getKey()); //henter data om shows fra databasen
                    //Calls the method getMovieSchedule that creates new buttons from the database
                    //with time and date for play
                }
            });
        }
    }

    @FXML
    private void getMovieSchedule(int movieId) {
        //gets times for a given movie from its movie id
        //Reset the schedule boxes
        upper_schedule.getChildren().clear(); //clears the container for elements
        lower_schedule.getChildren().clear(); //clears the container for elements

        LinkedHashMap<Integer, Timestamp> schedule = new LinkedHashMap(db.getMovieSchedule(movieId)); // sets the times into a linkedhashmap (showid as key (int) and timestamp as value (timestamp)

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
            if((times[i][0] != null) && (i>=7) && (i<14)) {
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
        overfillPane.toBack();
        dragMouse = false;
        seatsInOrder = new ArrayList<String>(); // initalizing the arraylist that will contain the seat(s) that has been clicked

        //dbcall så vi kan få information om forestillingen (hvor den vises, filmtitel osv.)
        sceneVBox.getChildren().clear(); //henter sædderne ind på sceneVBox - hvad gør clear??
        bh = db.getBuildSceneInfo(showId); //lægger infor om den enkelte films sceneopbygning ind i buildHolder
        movieNameLabel.setText(bh.getMovieName()); //sætter navnet på den pågældende film ind på movieNameLabel
        movieTimeLabel.setText("Tidspunkt: " + new SimpleDateFormat("dd/MM HH:mm").format(bh.getTime())); //viser tidspunkt for det show man ahr valgt
        cinemaNameLabel.setText(bh.getCinemaName()); //sætter navn på den pågældende sal
        int totalSeats = (bh.getColumns() * bh.getRows()); //regner antalet af total antal sædder i salen ud.
        int freeSeats = totalSeats - bh.getReservedNumber(); //regner antal af frie sædder ud.
        numberOfReserved.setText("Ledige pladser: " + freeSeats + " (I alt: " + totalSeats + ")"); //viser antal frie sædder i den pågældende sal
        numberOfSelectedSeats.setText("Valgte pladser: 0"); // viser antallet af valgte sæder

        int columns = bh.getColumns(); //henter kolonner/bredden i den pågældende sal
        int rows = bh.getRows(); //henter rækker/længden i den pågældende sal
        GridPane gp = new GridPane(); //skaber et gridpane som kolonner og rækker kan opbevares i.
        gp.setPrefSize(scenePane.getWidth()-5,scenePane.getHeight()-10); //sætter gridpane til samme str som det scenePane den ligger inden i.
        gp.setHgap(6); //sætter mellemrum mellem sædderne på horizontalt led
        gp.setVgap(6); //sætter mellemrum mellem sædderne på vertikalt ved.
        gp.setAlignment(Pos.CENTER); // centers the gridpane to the vbox
        Boolean[][] resSeat = bh.getResSeat();
        for(int i = 1; i < columns+1; i++) { //laver en forloppe der kører kolonerne igennem

            for(int j = 1; j < rows+1; j++) { //forlopp der kører rækkerne igennem
                double width = (scenePane.getWidth()-8*bh.getColumns()-8)/bh.getColumns(); // sets the width of the seat according to the cinema width
                double height = (scenePane.getHeight()-8*bh.getRows()-8)/bh.getRows(); // sets the height of the seat according to the cinema height
                final Rectangle r = new Rectangle(width,height); //laver sædderne som firkanter
                int x = i;
                int y = j;

                if(resSeat[i][j] != null) { // if current entity in array isnt null
                    if(resSeat[i][j]) { // if a seat is reserved we made its boolean true
                        r.setFill(Color.web("#E53935")); //sets the red color of a reserved seat
                    }
                } else {
                    r.setFill(Color.web("#43A047")); //sets the green color of a available seat


                    // when starting to drag
                    r.setOnDragDetected(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Dragboard db = r.startDragAndDrop(TransferMode.MOVE); // start dragndrop
                            ClipboardContent cc = new ClipboardContent(); // creates new clipboardcontent which is normally used to hold a value
                            cc.putString(""); // this is just to obtain the dragndrop function. we do not set the value to anything since we do not transfer value between the seats
                            db.setContent(cc); // binds the clipboardcontent to the dragboard since it's needed
                            db.setDragView(new Image("dragndrop.png")); // normally the dragview would be a reflection of the item dragged (the seat in this case) but we just want to select seats by holding down the mousebutton
                        }
                    });

                    // when dragging over a seat
                    r.setOnDragEntered(new EventHandler<DragEvent>() {
                        @Override
                        public void handle(DragEvent event) {
                            addSeatToOrder(r, x, y);
                        }
                    });


                    // funktion ved klik på ledigt sæde
                    r.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            addSeatToOrder(r, x, y);
                        } // function to run when an available seat is clicked
                    });
                }

                gp.add(r, i, j); //tilføj til gridpane: r= firkanterne, i=fælterne på x-aksen og j= felterne på y-aksen
            }

        }

        sceneVBox.getChildren().add(gp); //sætter gridpane ind i sceneVBox
        tabPane.getSelectionModel().select(1); //sætter det ind under "fane" nr 2.

    }

    private void addSeatToOrder(final Rectangle r, int x, int y) {

        // add to current order
        if(r.getFill().toString().contains("0x43a047ff")) { // if the seat is color code green
            r.setFill(Color.web("#039BE5")); // sets color to blue
            String seatString = (x + ":" + y); // seatString 3:3 etc.
            seatsInOrder.add(seatString); // adds seatString to array
            intChosenSeats++;
            numberOfSelectedSeats.setText("Valgte pladser: " + intChosenSeats); // viser antallet af valgte sæder

        } else if(r.getFill().toString().contains("0x039be5ff")) { // if the seat is color code blue
            r.setFill(Color.web("#43A047")); // set color to green
            String seatString = (x + ":" + y); // seatString 3:3 etc.
            seatsInOrder.remove(seatString); // removes seatString from array
            intChosenSeats--;
            numberOfSelectedSeats.setText("Valgte pladser: " + intChosenSeats); // viser antallet af valgte sæder
        }

    }

    @FXML
    private void makeReservation() throws Exception{

        if(!customerName.getText().isEmpty() && !customerPhone.getText().isEmpty() && seatsInOrder.size()>0 && customerPhone.getLength() == 8) { // if customer name and phone has been entered and at least one seat has been chosen
            String name = customerName.getText(); // gets the name of the customer from the textfield
            String phone = customerPhone.getText(); // gets the phonenumber of the customer from the textfield


            // if a reservation is completely inserted it will return true
            if(db.insertReservation(seatsInOrder, bh.getShowId(), customerName.getText(), customerPhone.getText())) {
                newPopUp("Bestillingen er gennemført");
                System.out.println("Bestillingen er gennemført.");
                seatsInOrder.clear(); // removes the chosen seats from the array
                customerName.clear(); // clears the textfield
                customerPhone.clear(); // clears the textfield
                buildReservationScene(bh.getShowId()); // builds an updated scene so that the new reservated seats are now available to pick
            } else {
                System.out.println("Der er sket en fejl - prøv igen!");

            }
        }
        else {
            System.out.println("Orderen kan ikke færdiggøres");
            newPopUp("Reservationen kan ikke udføres. \nFelterne navn og telfonnummer skal være udfyldt. \nDesuden skal du vælge sæder");
        }


    }

    //Method to call getReservations() method when pressed enter
    @FXML
    public void enterToCheck(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            getReservations();
        }
    }


    @FXML
    private void getReservations() {
        //reservationList.getItems().add("Interstellar 19/01 10:00");
        //final Button button = new Button("Rediger");
        reservationList.getItems().clear();
        String number = phoneNumber.getText();
        LinkedHashMap<Integer, String> reservations = new LinkedHashMap(db.getReservations(number));
        for(Map.Entry<Integer, String> reservation : reservations.entrySet()) {
            reservationList.getItems().addAll(reservation.getValue());
            reservationList.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        //buildReservationScene(reservation.getKey());
                        System.out.println("clicked on " + reservationList.getSelectionModel().getSelectedItem());
                    }
                }
            });
        }

    }

    private void buildEditReservationView(int reservationID){
        Stage editReservationView = new Stage();
        editReservationView.initStyle(StageStyle.UTILITY);

        //final Label

    }

    private void newPopUp(String text) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);

        final Button button = new Button("OK");
        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                dialog.close();
            }
        });

        final Label label = new Label(text);
        label.setPrefSize(300,70);
        label.setAlignment(Pos.CENTER);
        //label.textAlignmentProperty()


        dialog.initStyle(StageStyle.UNIFIED);
        Pane pane = new Pane();
        pane.setPrefWidth(300);
        pane.setPrefHeight(100);
        pane.getChildren().addAll(button, label);
        pane.setPrefWidth(320);
        pane.setPrefHeight(120);
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label,button);
        pane.getChildren().add(vbox);
        Scene scene = new Scene(pane);
        dialog.setScene(scene);
        dialog.show();
    }


}
