package cz.vse.java.pfej00.tymovyProjekt.builders;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PopupBuilder {

    /**
     * Statická metoda slouží k vyvolání popup hlášky
     * v případě, že neprošla frontendová validace
     * Parametr obsahuje stringový odkaz na html soubor
     * s patřičnou hláškou
     *
     * @param popup
     */
    public static void loadPopup(String popup) {
        WebView webView = new WebView();
        webView.getEngine().load(PopupBuilder.class.getResource(popup).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(new Scene(webView, 250, 250));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Error");
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> stage.close());
        delay.play();
        stage.show();
    }

    /**
     * Statická metoda slouží k vyvolání popup hlášky
     * v případě, že neprošla frontendová validace na HESLO
     * Parametr obsahuje stringový odkaz na html soubor
     * s hláškou na slabé heslo
     *
     * @param popup
     */
    public static void loadPopupForPassword(String popup) {
        WebView webView = new WebView();
        webView.getEngine().load(PopupBuilder.class.getResource(popup).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(new Scene(webView, 400, 400));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Error");
        PauseTransition delay = new PauseTransition(Duration.seconds(8));
        delay.setOnFinished(event -> stage.close());
        delay.play();
        stage.show();
    }
}
