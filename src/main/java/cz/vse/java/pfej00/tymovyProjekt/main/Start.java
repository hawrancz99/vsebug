package cz.vse.java.pfej00.tymovyProjekt.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;


public class Start extends Application
{
    /**
     * Metoda, prostřednictvím níž se spouští celá aplikace.
     *
     * @param args Parametry příkazového řádku
     */
    public static void main(String[] args)
    {
        launch();
    }

    /**
     * Metoda pro vyvolání GUI
     * @param primaryStage
     * @throws Exception
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("VŠEBUG");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
