package dictionary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application  {

    public static DictionaryManagement dm = new DictionaryManagement();

    public static void main(String[] args) {
        dm.insertFromFile();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("Stage1.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("Dictionaries.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
