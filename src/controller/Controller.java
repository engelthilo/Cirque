package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

    @FXML
    private Pane editReservationTopPane;

    private DBConnect db;

    private ArrayList<String> seatsInOrder;

    private buildHolder bh;

    private ArrayList<String> newSeats;

    private Stage editReservationView;

    private int intChosenSeats;

    private String dragColorCheck;

    public Controller() {
        db = new DBConnect();
    }


    @FXML
    private void initialize() {
        requestPhoneNumber();
        getMovies();

    }

    /**
     * Input: Nothing
     * Action: When ''Ret reservation'' is selected put focus on phoneNumber textfield
     * Output: Nothing
     */
    @FXML
    private void requestPhoneNumber(){
        //Observe which tab is selected.
        tabPane.getSelectionModel().selectedItemProperty().addListener (
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                        //If ''Ret reservation'' is clicked, do a Platform.runLater and inside this call phoneNumber.requestFocus()
                        if (tabPane.getSelectionModel().isSelected(2)){ //
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    phoneNumber.requestFocus();
                                }
                            });


                        }


                    }
                }
        );
    }

    /**
     * Input: Movies
     * Acion: Creates a button for every movie and calls the method getMovieSchedule
     * Output: Buttons with movie names
     */
    @FXML
    protected void getMovies() {
        LinkedHashMap<Integer, String> curMovies = new LinkedHashMap(db.getMovies());

        //For loop that finds and creates a button for every movie in the database [[MARK]]
        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) {
            final Button button = new Button(movie.getValue());
            button.setPrefWidth(200);
            showsVBox.getChildren().add(button);
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { //event when clicked on the created button
                    scheduleHeader.setText("Viser forestillinger for filmen: " + movie.getValue());
                    getMovieSchedule(movie.getKey()); //henter data om shows fra databasen
                }
            });
        }
    }

    /**
     * Input:Time of movies - Timestamps
     * Action: gets times for a given movie from its movie id
     * Output:
     */
    @FXML
    private void getMovieSchedule(int movieId) {
        upper_schedule.getChildren().clear();
        lower_schedule.getChildren().clear();

        LinkedHashMap<Integer, Timestamp> schedule = new LinkedHashMap(db.getMovieSchedule(movieId));

        //puts the timestamps in
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

    /**
     * Input: int showId
     * Action: Build the reservation scene in ''Reservation'' tab
     * Output: No output
     */
    @FXML
    private void buildReservationScene(int showId) { //denne metode bygger reservationScene for den pågældende film
        overfillPane.toBack();
        seatsInOrder = new ArrayList<String>(); // initalizing the arraylist that will contain the seat(s) that has been clicked
        intChosenSeats = 0;

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
        gp.setPrefSize(879,522); //sætter gridpane til samme str som det scenePane den ligger inden i.
        gp.setAlignment(Pos.CENTER); // centers the gridpane to the vbox
        Boolean[][] resSeat = bh.getResSeat();
        for(int i = 1; i < columns+1; i++) { //laver en forloppe der kører kolonerne igennem

            for(int j = 1; j < rows+1; j++) { //forlopp der kører rækkerne igennem
                double width = 870/(bh.getColumns()+1)-5;
                double height = 515/(bh.getRows()+1)-5;
                final Rectangle r = new Rectangle(width,height); //laver sædderne som firkanter
                r.setArcWidth(6);
                r.setArcHeight(6);
                int x = i;
                int y = j;
                r.setStroke(Color.TRANSPARENT);
                r.setStrokeWidth(5);

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
                            dragColorCheck = r.getFill().toString();
                            addSeatToOrder(r, x, y);
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
                            dragColorCheck = r.getFill().toString();
                            addSeatToOrder(r, x, y);
                        } // function to run when an available seat is clicked
                    });
                }
                gp.add(r, i, j); //tilføj til gridpane: r= firkanterne, i=fælterne på x-aksen og j= felterne på y-aksen
            }

        }

        String abcString = "ABCDEFGHIJKLMNOPQRSTUVXYZ";

        for(int i = 1; i < bh.getColumns()+1; i++) {
            for (int j = 1; j < bh.getRows() + 1; j++) {
                Label lbl = new Label(abcString.charAt(j-1) + "");
                lbl.setAlignment(Pos.CENTER);
                lbl.setPadding(new Insets(0,6,0,0));
                gp.add(lbl, 0, j);
            }
            Label lbl = new Label(i + "");
            lbl.setMinWidth(870 / (bh.getColumns() + 1));
            lbl.setAlignment(Pos.CENTER);
            lbl.setPadding(new Insets(0,0,6,0));
            gp.add(lbl, i, 0);
        }

        Label centerFix = new Label("");
        centerFix.setMinWidth(20);
        gp.add(centerFix, bh.getColumns()+2, 1);

        sceneVBox.getChildren().add(gp); //sætter gridpane ind i sceneVBox
        tabPane.getSelectionModel().select(1); //sætter det ind under "fane" nr 2.

    }

    /**
     * Input: Rectangle, x and y coordinates for the seats
     * Action: Creates the seats used in buildReservationScene.
     * Output: No out put
     */
    private void addSeatToOrder(final Rectangle r, int x, int y) {

        // add to current order
        if(r.getFill().toString().contains("0x43a047ff") && dragColorCheck.equals("0x43a047ff")) { // if the seat is color code green
            r.setFill(Color.web("#039BE5")); // sets color to blue
            String seatString = (x + ":" + y); // seatString 3:3 etc.
            seatsInOrder.add(seatString); // adds seatString to array
            intChosenSeats++;
            numberOfSelectedSeats.setText("Valgte pladser: " + intChosenSeats); // viser antallet af valgte sæder

        } else if(r.getFill().toString().contains("0x039be5ff") && dragColorCheck.equals("0x039be5ff")) { // if the seat is color code blue
            r.setFill(Color.web("#43A047")); // set color to green
            String seatString = (x + ":" + y); // seatString 3:3 etc.
            seatsInOrder.remove(seatString); // removes seatString from array
            intChosenSeats--;
            numberOfSelectedSeats.setText("Valgte pladser: " + intChosenSeats); // viser antallet af valgte sæder
        }

    }

    @FXML
    private void makeReservation() throws Exception {
        // if customer name and phone has been entered and at least one seat has been chosen
        if(!customerName.getText().isEmpty() && !customerPhone.getText().isEmpty() && seatsInOrder.size()>0 && customerPhone.getLength() == 8) {
            String name = customerName.getText(); // gets the name of the customer from the textfield
            String phone = customerPhone.getText(); // gets the phonenumber of the customer from the textfield


            // if a reservation is completely inserted it will return true
            if(db.insertReservation(seatsInOrder, bh.getShowId(), customerName.getText(), customerPhone.getText())) {
                newPopUp("Bestillingen er gennemført");
                seatsInOrder.clear(); // removes the chosen seats from the array
                customerName.clear(); // clears the textfield
                customerPhone.clear(); // clears the textfield
                buildReservationScene(bh.getShowId()); // builds an updated scene so that the new reservated seats are now available to pick
            } else {
                newPopUp("Der er sket en fejl!\nPrøv igen");
            }
        }
        else {
            newPopUp("Reservationen kan ikke udføres.\nFelterne navn og telefonnummer skal være udfyldt.\nDesuden skal du vælge sæder");
        }


    }

    //Method to call makeReservation() when pressed enter in name or phone number field
    @FXML
    public void reservationCheckEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                makeReservation();
            } catch (Exception e) {
                catchPopUp(e);
            }
        }
    }

    //Method to call getReservations() when pressed enter in phone number textfield
    @FXML
    public void editCheckEnter(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            getReservations();
        }
    }

    @FXML
    private void getReservations() {
        reservationList.getItems().clear();
        editReservationTopPane.setOpacity(1);

        String number = phoneNumber.getText();
        LinkedHashMap<Integer, HBox> reservations = new LinkedHashMap(db.getReservations(number));
        for(Map.Entry<Integer, HBox> reservation : reservations.entrySet()) {
            HBox hbox = new HBox(reservation.getValue());
            hbox.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        buildEditReservationView(reservation.getKey());
                    }
                }
            });

            reservationList.getItems().add(hbox);
        }

        if(reservations.size()==0) {
            reservationList.getItems().add("Der blev ikke fundet nogen reservationer på det ønskede nummer");
        }

    }

    private void buildEditReservationView(int reservationID){
        editReservationView = new Stage();
        editReservationView.initStyle(StageStyle.UTILITY);
        editReservationView.setResizable(false);

        Pane pane = new Pane();
        pane.setPrefSize(900,620);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.setSpacing(5);
        vbox.setPrefSize(900,600);

        VBox editVBox = new VBox();
        editVBox.setAlignment(Pos.CENTER);
        editVBox.setPrefSize(900,500);

        Pane buttonContainer = new Pane();
        buttonContainer.setPrefSize(900,50);

        Button editButton = new Button("Rediger reservation");
        Button deleteButton = new Button("Slet reservation");

        deleteButton.setLayoutX(22);
        deleteButton.setLayoutY(10);
        deleteButton.setStyle("-fx-background-color: #e53935; -fx-font-weight: bold; -fx-cursor: hand;");

        editButton.setLayoutX(716);
        editButton.setLayoutY(10);
        editButton.setStyle("-fx-background-color: #66BB6A; -fx-font-weight: bold; -fx-cursor: hand;");

        editButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                updateReservation(reservationID);
            }
        });

        deleteButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deleteReservation(reservationID);
            }
        });

        buttonContainer.getChildren().addAll(deleteButton,editButton);

        bh = db.getBuildSceneInfo(db.getShowIdFromResId(reservationID)); //lægger infor om den enkelte films sceneopbygning ind i buildHolder

        Label editReservationLabel = new Label(bh.getMovieName() + " - " + new SimpleDateFormat("dd/MM HH:mm").format(bh.getTime()));
        editReservationLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        vbox.getChildren().add(editReservationLabel);

        int columns = bh.getColumns(); //henter kolonner/bredden i den pågældende sal
        int rows = bh.getRows(); //henter rækker/længden i den pågældende sal
        GridPane gp = new GridPane(); //skaber et gridpane som kolonner og rækker kan opbevares i.
        gp.setPrefSize(900, 500); //sætter gridpane til samme str som det scenePane den ligger inden i.
        gp.setAlignment(Pos.CENTER); // centers the gridpane to the vbox
        Boolean[][] resSeat = bh.getResSeat();

        Boolean[][] editResSeat = db.getResSeat(reservationID, bh); //Get the reserved seats for the specifik reservation id
        newSeats = new ArrayList<String>();

        for(int i = 1; i < columns+1; i++) { //laver en forloppe der kører kolonerne igennem

            for (int j = 1; j < rows + 1; j++) { //forlopp der kører rækkerne igennem
                double width = 885/(bh.getColumns()+1)-5;
                double height = 490/(bh.getRows()+1)-5;

                final Rectangle r = new Rectangle(width, height); //laver sædderne som firkanter
                r.setArcWidth(6);
                r.setArcHeight(6);
                r.setStroke(Color.TRANSPARENT);
                r.setStrokeWidth(5);
                int x = i;
                int y = j;

                String seatString = "";

                if (resSeat[i][j] != null) { // if current entity in array isnt null
                    if (resSeat[i][j] && editResSeat[i][j] == null) { // if a seat is reserved we made its boolean true
                        r.setFill(Color.web("#E53935")); //sets the red color of a reserved seat
                    } else if (editResSeat[i][j] != null){
                        if(editResSeat[i][j]) {
                            seatString = x + ":" + y;
                            newSeats.add(seatString);
                            r.setFill(Color.web("#039BE5"));

                            /** DRAG FUNCTION FOR BLUE SEATS STARTS HERE */
                            // when starting to drag
                            r.setOnDragDetected(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    Dragboard db = r.startDragAndDrop(TransferMode.MOVE); // start dragndrop
                                    ClipboardContent cc = new ClipboardContent(); // creates new clipboardcontent which is normally used to hold a value
                                    cc.putString(""); // this is just to obtain the dragndrop function. we do not set the value to anything since we do not transfer value between the seats
                                    db.setContent(cc); // binds the clipboardcontent to the dragboard since it's needed
                                    db.setDragView(new Image("dragndrop.png")); // normally the dragview would be a reflection of the item dragged (the seat in this case) but we just want to select seats by holding down the mousebutton
                                    dragColorCheck = r.getFill().toString();
                                    addToEditSeatOrder(r, x, y);
                                }
                            });

                            // when dragging over a seat
                            r.setOnDragEntered(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    addToEditSeatOrder(r, x, y);
                                }
                            });


                            // funktion ved klik på ledigt sæde
                            r.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    dragColorCheck = r.getFill().toString();
                                    addToEditSeatOrder(r, x, y);
                                } // function to run when an available seat is clicked
                            });

                            /** DRAG FUNCTION FOR BLUE SEATS ENDS HERE */
                        }
                    }
                }  else {
                    r.setFill(Color.web("#43A047")); //sets the green color of a available seat

                    /** DRAG FUNCTION FOR GREEN SEATS STARTS HERE */
                    // when starting to drag
                    r.setOnDragDetected(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Dragboard db = r.startDragAndDrop(TransferMode.MOVE); // start dragndrop
                            ClipboardContent cc = new ClipboardContent(); // creates new clipboardcontent which is normally used to hold a value
                            cc.putString(""); // this is just to obtain the dragndrop function. we do not set the value to anything since we do not transfer value between the seats
                            db.setContent(cc); // binds the clipboardcontent to the dragboard since it's needed
                            db.setDragView(new Image("dragndrop.png")); // normally the dragview would be a reflection of the item dragged (the seat in this case) but we just want to select seats by holding down the mousebutton
                            dragColorCheck = r.getFill().toString();
                            addToEditSeatOrder(r, x, y);
                        }
                    });

                    // when dragging over a seat
                    r.setOnDragEntered(new EventHandler<DragEvent>() {
                        @Override
                        public void handle(DragEvent event) {
                            addToEditSeatOrder(r, x, y);
                        }
                    });


                    // funktion ved klik på ledigt sæde
                    r.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            dragColorCheck = r.getFill().toString();
                            addToEditSeatOrder(r, x, y);
                        } // function to run when an available seat is clicked
                    });

                    /** DRAG FUNCTION FOR GREEN SEATS ENDS HERE */

                }

                gp.add(r,i,j);
            }


        }

        String abcString = "ABCDEFGHIJKLMNOPQRSTUVXYZ";

        for(int i = 1; i < bh.getColumns()+1; i++) {
            for (int j = 1; j < bh.getRows() + 1; j++) {
                Label lbl = new Label(abcString.charAt(j-1) + "");
                lbl.setAlignment(Pos.CENTER);
                lbl.setPadding(new Insets(0,6,0,0));
                gp.add(lbl, 0, j);
            }
            Label lbl = new Label(i + "");
            lbl.setMinWidth(870 / (bh.getColumns() + 1));
            lbl.setAlignment(Pos.CENTER);
            lbl.setPadding(new Insets(0,0,6,0));
            gp.add(lbl, i, 0);
        }

        Label centerFix = new Label("");
        centerFix.setMinWidth(20);
        gp.add(centerFix, bh.getColumns()+2, 1);

        editVBox.getChildren().add(gp); //sætter gridpane ind i sceneVBox

        vbox.getChildren().addAll(editVBox, buttonContainer);

        pane.getChildren().add(vbox);

        Scene scene = new Scene(pane);
        editReservationView.setScene(scene);
        editReservationView.show();
    }

    private void addToEditSeatOrder(Rectangle r, int x, int y) {
        // add to current order (edit order)
        if(r.getFill().toString().contains("0x43a047ff") && dragColorCheck.equals("0x43a047ff")) { // if the seat is color code green
            r.setFill(Color.web("#039BE5")); // sets color to blue
            String seatString = (x + ":" + y); // seatString 3:3 etc.
            newSeats.add(seatString); // adds seatString to array
        } else if(r.getFill().toString().contains("0x039be5ff") && dragColorCheck.equals("0x039be5ff")) { // if the seat is color code blue
            r.setFill(Color.web("#43A047")); // set color to green
            String seatString = (x + ":" + y); // seatString 3:3 etc.
            newSeats.remove(seatString); // removes seatString from array
        }
    }

    private void updateReservation(int reservationID) {
        if(db.updateReservation(newSeats, reservationID)) {
            newPopUp("Reservationen er rettet!");
            editReservationView.close();
            getReservations();
            overfillPane.toFront();
        } else {
            newPopUp("Der er sket en fejl!\nReservationen kunne ikke rettes. \nLuk vinduet og prøv igen.");
        }
    }

    private void deleteReservation(int reservationID) {
        if(db.deleteReservation(reservationID)) {
            newPopUp("Reservationen er slettet!");
            editReservationView.close();
            getReservations();
            overfillPane.toFront();
        } else {
            newPopUp("Der er sket en fejl!\nReservationen kunne ikke slettes. \nLuk vinduet og prøv igen.");
        }
    }


    private void newPopUp(String text) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);

        final Button button = new Button("OK");
        button.requestFocus();
        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                dialog.close();
            }
        });

        final Label label = new Label(text);
        label.setPrefSize(300,70);
        label.setAlignment(Pos.CENTER);


        dialog.initStyle(StageStyle.UNIFIED);
        Pane pane = new Pane();
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

    public void catchPopUp(Exception e) {
        newPopUp("Der er opstået en fejl.\nFejl: " + e);
    }

}
