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
        stage.setScene(new Scene(webView, 250, 250));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("error");
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> stage.close());
        delay.play();
        stage.show();
    }

    public static void loadWaitPopup(String popup){
        WebView webView = new WebView();
        webView.getEngine().load(PopupBuilder.class.getResource(popup).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(new Scene(webView, 250, 250));
        stage.initStyle(StageStyle.UNDECORATED);
        //to bude bez delaye, bude se to kroutit dokud nepřijde odpověď
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> stage.close());
        delay.play();
        stage.show();
    }
}
