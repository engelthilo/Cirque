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

import java.sql.SQLException;
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

    public Controller() throws SQLException {
        db = new DBConnect();
    }


    @FXML
    private void initialize() throws Exception {
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
    protected void getMovies() throws SQLException {
        LinkedHashMap<Integer, String> curMovies = new LinkedHashMap(db.getMovies());

        //For loop that finds and creates a button for every movie in the database [[MARK]]
        for(Map.Entry<Integer, String> movie : curMovies.entrySet()) {
            final Button button = new Button(movie.getValue());
            button.setPrefWidth(200);
            showsVBox.getChildren().add(button);
            button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    scheduleHeader.setText("Viser forestillinger for filmen: " + movie.getValue());

                    try {
                        getMovieSchedule(movie.getKey()); //henter data om shows fra databasen
                    } catch (SQLException ex) {
                        newPopUp("Der skete en fejl. Prøv igen!");
                    }
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
    private void getMovieSchedule(int movieId) throws SQLException {
        upper_schedule.getChildren().clear();
        lower_schedule.getChildren().clear();

        LinkedHashMap<Integer, Timestamp> schedule = new LinkedHashMap(db.getMovieSchedule(movieId));

        Timestamp[][] times = new Timestamp[14][6];
        int[][] showIds = new int[14][6];
        int i = -1;
        int j = 0;
        String lastShow = "";

        //Runs the schedule for the selected movie through and print out the date for them
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


            if((times[i][0] != null) && (i<7)) {
                TitledPane tp = new TitledPane();
                tp.setText(new SimpleDateFormat("dd/MM").format(times[i][0]));
                VBox vb = new VBox();
                vb.setSpacing(15);
                for (j = 0; j < 6; j++) {


                    if (times[i][j] != null) {
                        final int showId = showIds[i][j];
                        final Button button = new Button(new SimpleDateFormat("HH:mm").format(times[i][j]));
                        button.setPrefWidth(100);
                        button.setPadding(new Insets(10, 10, 10, 10));
                        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {

                                try {
                                    buildReservationScene(showId);
                                } catch (SQLException ex) {
                                    newPopUp("Der skete en fejl. Prøv igen!");
                                }
                            } //ved eventet bliver den pågældende resevations side bygget - alt efter hvilken film
                        });
                        vb.getChildren().add(button);
                    }
                }
                tp.setContent(vb);
                upper_schedule.getChildren().add(tp);
            }


            if((times[i][0] != null) && (i>=7) && (i<14)) {
                TitledPane tp = new TitledPane();
                tp.setText(new SimpleDateFormat("dd/MM").format(times[i][0]));
                VBox vb = new VBox();
                vb.setSpacing(15);
                for (j = 0; j < 6; j++) {


                    if (times[i][j] != null) {
                        final int showId = showIds[i][j];
                        final Button button = new Button(new SimpleDateFormat("HH:mm").format(times[i][j]));
                        button.setPrefWidth(100);
                        button.setPadding(new Insets(15, 15, 15, 15));
                        button.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                try {
                                    buildReservationScene(showId);
                                } catch (SQLException ex) {
                                    newPopUp("Der skete en fejl. Prøv igen!");
                                }
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
     * Input: int showId. Id for specific show, time and date.
     * Action: Build the reservation scene in ''Reservation'' tab
     * Output: Manipulated JavaFX scene
     */
    @FXML
    private void buildReservationScene(int showId) throws SQLException {
        overfillPane.toBack();
        seatsInOrder = new ArrayList<String>(); // initalizing the arraylist that will contain the seat(s) that has been clicked
        intChosenSeats = 0;


        sceneVBox.getChildren().clear();
        bh = db.getBuildSceneInfo(showId);
        movieNameLabel.setText(bh.getMovieName());
        movieTimeLabel.setText("Tidspunkt: " + new SimpleDateFormat("dd/MM HH:mm").format(bh.getTime()));
        cinemaNameLabel.setText(bh.getCinemaName());
        int totalSeats = (bh.getColumns() * bh.getRows());
        int freeSeats = totalSeats - bh.getReservedNumber();
        numberOfReserved.setText("Ledige pladser: " + freeSeats + " (I alt: " + totalSeats + ")");
        numberOfSelectedSeats.setText("Valgte pladser: 0");

        int columns = bh.getColumns();
        int rows = bh.getRows();
        GridPane gp = new GridPane();
        gp.setPrefSize(879,522);
        gp.setAlignment(Pos.CENTER);
        Boolean[][] resSeat = bh.getResSeat();
        for(int i = 1; i < columns+1; i++) {

            for(int j = 1; j < rows+1; j++) {
                double width = 870/(bh.getColumns()+1)-5;
                double height = 515/(bh.getRows()+1)-5;
                final Rectangle r = new Rectangle(width,height);
                r.setArcWidth(6);
                r.setArcHeight(6);
                int x = i;
                int y = j;
                r.setStroke(Color.TRANSPARENT);
                r.setStrokeWidth(5);

                if(resSeat[i][j] != null) {
                    if(resSeat[i][j]) {
                        r.setFill(Color.web("#E53935")); //sets the red color of a reserved seat
                    }
                } else {
                    r.setFill(Color.web("#43A047")); //sets the green color of a available seat

                    // when starting to drag
                    r.setOnDragDetected(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putString("");
                            db.setContent(cc);
                            db.setDragView(new Image("dragndrop.png"));
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



                    r.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            dragColorCheck = r.getFill().toString();
                            addSeatToOrder(r, x, y);
                        }
                    });
                }
                gp.add(r, i, j);
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

        sceneVBox.getChildren().add(gp);
        tabPane.getSelectionModel().select(1);
    }

    /**
     * Input: Rectangle, x and y coordinates for the seats
     * Action: Creates the seats used in buildReservationScene.
     * Output: Seats in buildReservation
     */
    private void addSeatToOrder(final Rectangle r, int x, int y) {


        if(r.getFill().toString().contains("0x43a047ff") && dragColorCheck.equals("0x43a047ff")) {
            r.setFill(Color.web("#039BE5"));
            String seatString = (x + ":" + y);
            seatsInOrder.add(seatString);
            intChosenSeats++;
            numberOfSelectedSeats.setText("Valgte pladser: " + intChosenSeats);

        } else if(r.getFill().toString().contains("0x039be5ff") && dragColorCheck.equals("0x039be5ff")) {
            r.setFill(Color.web("#43A047"));
            String seatString = (x + ":" + y);
            seatsInOrder.remove(seatString);
            intChosenSeats--;
            numberOfSelectedSeats.setText("Valgte pladser: " + intChosenSeats);
        }

    }

    /**
     * Input: No input
     * Action: Saves the current reservation in the database
     * Output: No output
     */
    @FXML
    private void makeReservation() throws Exception {

        if(!customerName.getText().isEmpty() && !customerPhone.getText().isEmpty() && seatsInOrder.size()>0 && customerPhone.getLength() == 8) {
            String name = customerName.getText();
            String phone = customerPhone.getText();

            try {
                db.insertReservation(seatsInOrder, bh.getShowId(), customerName.getText(), customerPhone.getText());

                newPopUp("Bestillingen er gennemført");
                seatsInOrder.clear(); // removes the chosen seats from the array
                customerName.clear(); // clears the textfield
                customerPhone.clear(); // clears the textfield
                buildReservationScene(bh.getShowId()); // builds an updated scene so that the new reservated seats are now available to pick
            } catch (SQLException ex) {
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
    public void editCheckEnter(KeyEvent event) throws SQLException {
        if (event.getCode() == KeyCode.ENTER) {
            getReservations();
        }
    }

    /**
     * Input: Uses phoneNumber but takes no parameter
     * Action: Loads the reservation(s) for a given phoneNumber (is called from ''Ret reservation'' when the button i clicked
     * or when enter is pressed.
     * Output: HBox(s) with reservation for the phoneNumber input.
     */
    @FXML
    private void getReservations() throws SQLException {
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
                        try {
                            buildEditReservationView(reservation.getKey());
                        } catch (SQLException ex) {
                            newPopUp("Der skete en fejl. Prøv igen!");
                        }
                    }
                }
            });

            reservationList.getItems().add(hbox);
        }

        if(reservations.size()==0) {
            reservationList.getItems().add("Der blev ikke fundet nogen reservationer på det ønskede nummer");
        }

    }

    /**
     * Input: The reservationID from getReservations()
     * Action: Build scene that show the reservations for a current showID and phoneNumber
     * Output: popup window
     */
    private void buildEditReservationView(int reservationID) throws SQLException {
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

        bh = db.getBuildSceneInfo(db.getShowIdFromResId(reservationID));

        Label editReservationLabel = new Label(bh.getMovieName() + " - " + new SimpleDateFormat("dd/MM HH:mm").format(bh.getTime()));
        editReservationLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        vbox.getChildren().add(editReservationLabel);

        int columns = bh.getColumns();
        int rows = bh.getRows();
        GridPane gp = new GridPane();
        gp.setPrefSize(900, 500);
        gp.setAlignment(Pos.CENTER);
        Boolean[][] resSeat = bh.getResSeat();

        Boolean[][] editResSeat = db.getResSeat(reservationID, bh);
        newSeats = new ArrayList<String>();

        for(int i = 1; i < columns+1; i++) {

            for (int j = 1; j < rows + 1; j++) {
                double width = 885/(bh.getColumns()+1)-5;
                double height = 490/(bh.getRows()+1)-5;

                final Rectangle r = new Rectangle(width, height);
                r.setArcWidth(6);
                r.setArcHeight(6);
                r.setStroke(Color.TRANSPARENT);
                r.setStrokeWidth(5);
                int x = i;
                int y = j;

                String seatString = "";

                if (resSeat[i][j] != null) {
                    if (resSeat[i][j] && editResSeat[i][j] == null) {
                        r.setFill(Color.web("#E53935"));
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
                                    Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
                                    ClipboardContent cc = new ClipboardContent();
                                    cc.putString("");
                                    db.setContent(cc);
                                    db.setDragView(new Image("dragndrop.png"));
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
                    r.setFill(Color.web("#43A047"));

                    /** DRAG FUNCTION FOR GREEN SEATS STARTS HERE */
                    // when starting to drag
                    r.setOnDragDetected(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Dragboard db = r.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putString("");
                            db.setContent(cc);
                            db.setDragView(new Image("dragndrop.png"));
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


                    r.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            dragColorCheck = r.getFill().toString();
                            addToEditSeatOrder(r, x, y);
                        }
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

        editVBox.getChildren().add(gp);

        vbox.getChildren().addAll(editVBox, buttonContainer);

        pane.getChildren().add(vbox);

        Scene scene = new Scene(pane);
        editReservationView.setScene(scene);
        editReservationView.show();
    }

    //Used to add seats to current reservations. Used in buildEditReservationView.
    private void addToEditSeatOrder(Rectangle r, int x, int y) {
        // add to current order (edit order)
        if(r.getFill().toString().contains("0x43a047ff") && dragColorCheck.equals("0x43a047ff")) {
            r.setFill(Color.web("#039BE5"));
            String seatString = (x + ":" + y);
            newSeats.add(seatString);
        } else if(r.getFill().toString().contains("0x039be5ff") && dragColorCheck.equals("0x039be5ff")) {
            r.setFill(Color.web("#43A047"));
            String seatString = (x + ":" + y);
            newSeats.remove(seatString);
        }
    }

    //Called when ''Rediger reservations'' button in buildEditReservationView is pressed, saves the edited reservation to the database
    private void updateReservation(int reservationID) {
        try {
            db.updateReservation(newSeats, reservationID);

            newPopUp("Reservationen er rettet!");
            editReservationView.close();
            getReservations();
            overfillPane.toFront();
        } catch (SQLException ex) {
            newPopUp("Der er sket en fejl!\nReservationen kunne ikke rettes. \nLuk vinduet og prøv igen.");
        }
    }

    //Used to delete the reservation from the database
    private void deleteReservation(int reservationID) {
        try {
            db.deleteReservation(reservationID);

            newPopUp("Reservationen er slettet!");
            editReservationView.close();
            getReservations();
            overfillPane.toFront();
        } catch (SQLException ex) {
            newPopUp("Der er sket en fejl!\nReservationen kunne ikke slettes. \nLuk vinduet og prøv igen.");
        }
    }

    //Method used to creates warning/information popups through the program.
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

    //Warning popup
    public void catchPopUp(Exception e) {
        newPopUp("Der er opstået en fejl.\nFejl: " + e);
    }

}
