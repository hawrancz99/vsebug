package cz.vse.java.pfej00.tymovyProjekt.builders;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PopupBuilder {

    public static void loadPopup(String popup){
        WebView webView = new WebView();
        webView.getEngine().load(PopupBuilder.class.getResource(popup).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(new Scene(webView, 200, 200));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Error");
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> stage.close());
        delay.play();
        stage.show();
    }
}
