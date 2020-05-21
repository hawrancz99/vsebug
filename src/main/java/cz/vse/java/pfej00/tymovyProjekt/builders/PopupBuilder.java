package cz.vse.java.pfej00.tymovyProjekt.builders;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupBuilder {

    public static void loadPopup(String popup){
        WebView webView = new WebView();
        webView.getEngine().load(PopupBuilder.class.getResource(popup).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(new Scene(webView, 200, 200));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Error");
        stage.show();

        //nečitelný text, nevim proč
      /*  long mTime = System.currentTimeMillis();
        long end = mTime + 2000; // 5 seconds
        while (mTime < end)
        {
            mTime = System.currentTimeMillis();
        } */
    }
}
