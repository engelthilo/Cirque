import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/startscheme.fxml"));
        primaryStage.setTitle("BioBookingSystem");
        primaryStage.setScene(new Scene(root, 1010, 710));
        primaryStage.setMinHeight(740);
        primaryStage.setMinWidth(1010);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}